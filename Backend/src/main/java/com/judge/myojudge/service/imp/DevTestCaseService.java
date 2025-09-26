package com.judge.myojudge.service.imp;

import com.judge.myojudge.exception.ProblemNotFoundException;
import com.judge.myojudge.model.dto.ExecuteTestCase;
import com.judge.myojudge.model.dto.TestcaseDTO;
import com.judge.myojudge.model.entity.Problem;
import com.judge.myojudge.model.entity.TestCase;
import com.judge.myojudge.repository.ProblemRepo;
import com.judge.myojudge.repository.TestCaseRepo;
import com.judge.myojudge.service.ProblemService;
import com.judge.myojudge.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Profile("dev")
public class DevTestCaseService implements TestCaseService {

    private final ProblemService problemService;
    private final TestCaseRepo testCaseRepo;
    private final ProblemRepo problemRepo;

    @Value("${testcase.folder.path}")
    public String testCaseFolderPath;

        @Override
        public void saveTestCases(String handle, String name, List<MultipartFile> testCaseFiles) throws IOException {
         for(MultipartFile testCaseFile:testCaseFiles)
      {
//            // Save file to project folder
////            String fileName = UUID.randomUUID() + "_" + testCaseFile.getOriginalFilename();
            String fileName = handle + "_" + testCaseFile.getOriginalFilename();
////        System.out.println("Test: "+UUID.randomUUID());
            Path filePath = Paths.get(testCaseFolderPath, fileName);
//            // Files.createDirectories(filePath.getParent());//Automatically directory create for file save
            Files.write(filePath, testCaseFile.getBytes());
////        long fileSize=Files.size(filePath);
////        System.out.println("Size: "+fileSize);

            TestCase testCase=new TestCase();
            testCase.setFileName(testCaseFile.getOriginalFilename());
            testCase.setFilePath(filePath.toString());
            testCase.setHandle(handle);
            Optional<Problem> problem=problemService.findProblemByHandle(handle);
            if(problem.isEmpty()){
                throw new ProblemNotFoundException("Problem Not Found Handle By: "+handle);
            }
            testCase.setProblem(problem.get());
            testCaseRepo.save(testCase);

        }


//        return problem;
    }

    @Override
    public List<ExecuteTestCase> getTestCaseWithFile(Long problemId) {
        Optional<Problem> problem = problemRepo.findById(problemId);
        if(problem.isEmpty()){
            throw new ProblemNotFoundException("Problem Not Found With ID: "+problemId);
        }
        List<TestCase> testCases=problem.get().getTestcases();
        List<ExecuteTestCase> executeTestCases=new ArrayList<>();
        List<ExecuteTestCase> inputs=new ArrayList<>();
        List<ExecuteTestCase> outputs=new ArrayList<>();
        for(TestCase testCase:testCases){
            String lines=readFileLines(testCase.getFilePath());
            ExecuteTestCase executeTestCase=ExecuteTestCase.builder()
                    .title(testCase.getFileName())
                    .handle(testCase.getHandle())
                    .build();
            if(testCase.getFileName().endsWith(".in")){
                executeTestCase.setInput(lines);
                inputs.add(executeTestCase);
            }
            else{
                executeTestCase.setOutput(lines);
                outputs.add(executeTestCase);
            }
        }
        if(inputs.size()!=outputs.size()){
            throw new RuntimeException("Number of Input and Output file  not match");
        }
        for(int i=0;i<inputs.size();i++){
            ExecuteTestCase exTestCase=new ExecuteTestCase();
            exTestCase.setInput(inputs.get(i).getInput());
            exTestCase.setOutput(outputs.get(i).getOutput());
            exTestCase.setTitle(inputs.get(i).getTitle());
            exTestCase.setHandle(inputs.get(i).getHandle());
            executeTestCases.add(exTestCase);
        }
        return executeTestCases;
    }

    private String readFileLines(String filePath) {
        StringBuilder lines= new StringBuilder();
        Path path = Paths.get(filePath);

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
        return lines.toString();
    }

}
