package com.judge.myojudge.service.imp;

import com.judge.myojudge.exception.ProblemNotFoundException;
import com.judge.myojudge.model.dto.ProblemDTO;
import com.judge.myojudge.model.dto.ProblemWithSample;
import com.judge.myojudge.model.entity.Problem;
import com.judge.myojudge.model.entity.TestCase;
import com.judge.myojudge.repository.ProblemRepo;
import com.judge.myojudge.repository.TestCaseRepo;
import com.judge.myojudge.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Profile("dev")
public class DevProblemService implements ProblemService {

    @Value("${testcase.folder.path}")
    public String testCaseFolderPath;
    private final ProblemRepo problemRepo;
    private final TestCaseRepo testCaseRepo;

    @Override
    public void saveProblem(String title,
                            String handle,
                            String difficulty,
                            String type,
                            Long coin,
                            String problemStatement
    )  {
        Problem problem=new Problem();
        problem.setTitle(title);
        problem.setHandleName(handle);
        problem.setDifficulty(difficulty);
        problem.setType(type);
        problem.setCoins(coin);
        problem.setProblemStatement(problemStatement);
        problemRepo.save(problem);
        System.out.println("Problem Saved Successfully in Problems Function");
    }

    @Override
    public void saveProblemWithId(long id, String title, String handle, String difficulty, String type,
                                  String problemStatement, List<MultipartFile> multipartFiles) {

        Problem problem = problemRepo.findById(id)
                .orElseThrow(() -> new ProblemNotFoundException("Problem not found with ID: " + id));

        problem.setTitle(title);
        problem.setHandleName(handle);
        problem.setDifficulty(difficulty);
        problem.setType(type);
        problem.setProblemStatement(problemStatement);
        problemRepo.save(problem);

        // Save Test Case Files if provided
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            Optional<Problem> tempProblem = problemRepo.findByHandleName(handle);
            if(tempProblem.isEmpty()){
                throw new ProblemNotFoundException("Problem not found with handle: "+handle);
            }
            for (MultipartFile testCaseFile : multipartFiles) {
                if (!testCaseFile.isEmpty()) {
                    String fileName = handle + "_" + testCaseFile.getOriginalFilename();
                    Path filePath = Paths.get(testCaseFolderPath, fileName);

                    // Save file
                    try {
                        Files.write(filePath, testCaseFile.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    // Save test case details
                    TestCase testCase = new TestCase();
                    testCase.setFileName(testCaseFile.getOriginalFilename());
                    testCase.setFilePath(filePath.toString());
                    testCase.setHandle(handle);
                    testCase.setProblem(tempProblem.get());
                    testCaseRepo.save(testCase);
                }
            }
        }
    }


    private List<String> readFileLines(String filePath) {
        List<String> lines = new ArrayList<>();
        Path path = Paths.get(filePath);

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }

        return lines;
    }


    public void deleteEachProblem() throws IOException {
        if(problemRepo.count()==0) {
            throw new ProblemNotFoundException("No problems found to delete");
        };
        List<Problem>problems=problemRepo.findAll();
        for(Problem problem:problems)
        {
            for(TestCase testCase:problem.getTestcases())
            {
                Path path = Paths.get(testCase.getFilePath()); // Convert the file path string to a Path object
                if (Files.exists(path)) {
                    Files.delete(path); // Delete the file
                }
            }

        }

        problemRepo.deleteAll();
    }


    public ProblemWithSample findProblemByID(Long id) {
        Problem problem = problemRepo.findById(id).orElseThrow(() -> new RuntimeException("Problem not found"));
        ProblemWithSample problemWithSample = new ProblemWithSample();
        problemWithSample.setId(problem.getId());
        problemWithSample.setTitle(problem.getTitle());
        problemWithSample.setProblemStatement(problem.getProblemStatement());
        problemWithSample.setDifficulty(problem.getDifficulty());
        problemWithSample.setType(problem.getType());
        problemWithSample.setHandle(problem.getHandleName());
        problemWithSample.setCoins(problem.getCoins());


        // Find the input and output test cases
        TestCase sampleTestcase = null;
        TestCase sampleOutput = null;

        // Loop through test cases to find the relevant ones
        for (TestCase testCase : problem.getTestcases()) {
            if ("1.in".equals(testCase.getFileName())) {
                sampleTestcase = testCase;
            } else if ("1.out".equals(testCase.getFileName())) {
                sampleOutput = testCase;
            }
        }

        // Ensure both sampleTestcase and sampleOutput are found before proceeding
        if (sampleTestcase != null && sampleOutput != null) {
            problemWithSample.setSampleTestcase(readFileLines(sampleTestcase.getFilePath()));
            problemWithSample.setSampleOutput(readFileLines(sampleOutput.getFilePath()));
        } else {
            throw new RuntimeException("Test cases not found for problem ID: " + id);
        }

        problemWithSample.getSampleTestcase().forEach(System.out::println);
        problemWithSample.getSampleOutput().forEach(System.out::println);

        return problemWithSample;
    }

    public List<ProblemWithSample> findProblemAll() {
        List<ProblemWithSample>problemList=new ArrayList<>();
        List<Problem>problems= problemRepo.findAll();
        if(problems.isEmpty()){
            throw new ProblemNotFoundException("There is no problem..!");
        }
        for(Problem problem:problems)
        {
            ProblemWithSample problemWithSample=new ProblemWithSample();
            problemWithSample.setId(problem.getId());
            problemWithSample.setTitle(problem.getTitle());
            problemWithSample.setHandle(problem.getHandleName());
            problemWithSample.setProblemStatement(problem.getProblemStatement());
            problemWithSample.setType(problem.getType());
            problemWithSample.setDifficulty(problem.getDifficulty());

            TestCase sampleTestcase=new TestCase();
            TestCase sampleOutput=new TestCase();
            for(TestCase testCase:problem.getTestcases())
            {
                if(testCase.getFileName().equals("1.in")) {
                    sampleTestcase=testCase;
                }
                else if(testCase.getFileName().equals("1.out")) {
                    sampleOutput=testCase;
                }
            }

            Path file1=Paths.get(sampleTestcase.getFilePath());
            Path file2=Paths.get(sampleOutput.getFilePath());

            List<String> lines = new ArrayList<>();

            try (BufferedReader reader = Files.newBufferedReader(file1)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            problemWithSample.setSampleTestcase(lines);
            lines.clear();
            // Read the file line by line
            try (BufferedReader reader = Files.newBufferedReader(file2)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//            for(String s:lines) System.out.println(s);
            problemWithSample.setSampleOutput(lines);
            problemList.add(problemWithSample);
        }
        return problemList;
    }

    @Override
    public boolean findProblemByHandleExit(String handle) {
        return problemRepo.existsByHandleName(handle);
    }
    @Override
    public Optional<Problem> findProblemByHandle(String handle) {
        return problemRepo.findByHandleName(handle);
    }

    @Override
    public void deleteProblemByHandle(String handle) throws IOException {
        Optional<Problem> problem=problemRepo.findByHandleName(handle);
        if(problem.isEmpty()){
            throw new ProblemNotFoundException("Problem Not Found Handle By: "+handle);
        }

        for(TestCase testCase:problem.get().getTestcases())
        {
            Path path = Paths.get(testCase.getFilePath()); // Convert the file path string to a Path object
            if (Files.exists(path)) {
                Files.delete(path); // Delete the file
            }
        }
        problemRepo.delete(problem.get());
    }

    public ProblemDTO fetchOneProblemByID(long id){
        Optional<Problem> problem= problemRepo.findById(id);
        if(problem.isEmpty()){
            throw new ProblemNotFoundException("Problem not found with ID: "+id);
        }
        ProblemDTO problemDTO=new ProblemDTO();
        problemDTO.setTitle(problem.get().getTitle());
        problemDTO.setDifficulty(problem.get().getDifficulty());
        problemDTO.setType(problem.get().getType());
        problemDTO.setHandle(problem.get().getHandleName());
        problemDTO.setCoins(problem.get().getCoins());
        problemDTO.setProblemStatement(problem.get().getProblemStatement());
        return problemDTO;
    }

    @Override
    public Page<ProblemWithSample> findProblemAllByCategory(String category,String search,String difficulty, Pageable pageable) {
        List<ProblemWithSample> problemWithSamples=new ArrayList<>();
        Page<Problem> problems=problemRepo.findByType(category,search,difficulty,pageable);
        if(problems.isEmpty()){
            throw new ProblemNotFoundException("There is no problem By "+category+" category..!");
        }
        for(Problem problem:problems.getContent())
        {
            TestCase sampleTestcase = null;
            TestCase sampleOutput = null;

            for (TestCase testCase : problem.getTestcases()) {
                if (testCase.getFileName().equals("1.in")) {
                    sampleTestcase = testCase;
                } else if (testCase.getFileName().equals("1.out")) {
                    sampleOutput = testCase;
                }
            }

            List<String> sampleTestcaseContent = readFileLines(sampleTestcase.getFilePath());
            List<String> sampleOutputContent = readFileLines(sampleOutput.getFilePath());

            ProblemWithSample problemWithSample = ProblemWithSample.builder()
                    .id(problem.getId())
                    .problemStatement(problem.getProblemStatement())
                    .title(problem.getTitle())
                    .handle(problem.getHandleName())
                    .type(problem.getType())
                    .difficulty(problem.getDifficulty())
                    .coins(problem.getCoins())
                    .sampleTestcase(sampleTestcaseContent)
                    .sampleOutput(sampleOutputContent)
                    .build();
            problemWithSamples.add(problemWithSample);

        }
        return new PageImpl<>(problemWithSamples,pageable,problems.getTotalElements()) ;
    }
}
