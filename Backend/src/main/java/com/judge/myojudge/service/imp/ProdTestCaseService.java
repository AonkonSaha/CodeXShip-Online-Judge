package com.judge.myojudge.service.imp;

import com.cloudinary.Cloudinary;
import com.judge.myojudge.exception.ProblemNotFoundException;
import com.judge.myojudge.model.dto.ExecuteTestCase;
import com.judge.myojudge.model.entity.Problem;
import com.judge.myojudge.model.entity.TestCase;
import com.judge.myojudge.repository.ProblemRepo;
import com.judge.myojudge.repository.TestCaseRepo;
import com.judge.myojudge.service.CloudinaryService;
import com.judge.myojudge.service.ProblemService;
import com.judge.myojudge.service.TestCaseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Profile("prod")
public class ProdTestCaseService implements TestCaseService {

    private final TestCaseRepo testCaseRepo;
    private final ProblemService problemService;
    private final Cloudinary cloudinary;
    private final CloudinaryService cloudinaryService;
    private final ProblemRepo problemRepo;

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void saveTestCases(String handle, String name, List<MultipartFile> testCaseFiles) throws IOException {
        Optional<Problem> problem = problemService.findProblemByHandle(handle);

        if (problem.isEmpty()) {
            throw new ProblemNotFoundException("Problem Not Found Handle By: " + handle);
        }

        for (MultipartFile file : testCaseFiles) {
            Map uploadResult = cloudinaryService.uploadTestcase(file);
            String fileUrl = uploadResult.get("secure_url").toString();
            TestCase testCase = TestCase.builder()
                    .fileName(file.getOriginalFilename())
                    .filePath(fileUrl)
                    .handle(handle)
                    .fileKey(uploadResult.get("public_id").toString())
                    .build();

            testCase.setProblem(problem.get());
            testCaseRepo.save(testCase);
        }

    }

    @Override
    public List<ExecuteTestCase> getTestCaseWithFile(Long problemId) {
        Problem problem = problemRepo.findById(problemId)
                .orElseThrow(() -> new ProblemNotFoundException("Problem Not Found With ID: " + problemId));

        List<TestCase> testCases = problem.getTestcases();
        List<ExecuteTestCase> executeTestCases = new ArrayList<>();
        List<ExecuteTestCase> inputs = new ArrayList<>();
        List<ExecuteTestCase> outputs = new ArrayList<>();

        for (TestCase testCase : testCases) {
            String content = cloudinaryService.readCloudinaryFileForExecuting(testCase.getFilePath());

            ExecuteTestCase executeTestCase = ExecuteTestCase.builder()
                    .title(testCase.getFileName())
                    .handle(testCase.getHandle())
                    .build();

            if (testCase.getFileName().endsWith(".in")) {
                executeTestCase.setInput(content);
                inputs.add(executeTestCase);
            } else {
                executeTestCase.setOutput(content);
                outputs.add(executeTestCase);
            }
        }

        if (inputs.size() != outputs.size()) {
            throw new RuntimeException("Number of Input and Output files do not match");
        }

        for (int i = 0; i < inputs.size(); i++) {
            ExecuteTestCase exTestCase = new ExecuteTestCase();
            exTestCase.setInput(inputs.get(i).getInput());
            exTestCase.setOutput(outputs.get(i).getOutput());
            exTestCase.setTitle(inputs.get(i).getTitle());
            exTestCase.setHandle(inputs.get(i).getHandle());
            executeTestCases.add(exTestCase);
        }

        return executeTestCases;
    }

    @Override
    @Transactional
    public ExecuteTestCase getSampleTestCaseWithFile(Long problemId) {
        Problem problem = problemRepo.findById(problemId)
                .orElseThrow(() -> new ProblemNotFoundException("Problem Not Found With ID: " + problemId));
        List<TestCase> testCases = problem.getTestcases();

        ExecuteTestCase sampleTestCase = new ExecuteTestCase();

        boolean isGetSampleInput=false,isGetSampleOutput=false;
        for (TestCase testCase : testCases) {
            if(isGetSampleInput && isGetSampleOutput) break;
            String content = cloudinaryService.readCloudinaryFileForExecuting(testCase.getFilePath());
            sampleTestCase.setTitle(testCase.getFileName());
            sampleTestCase.setHandle(testCase.getHandle());
            if (testCase.getFileName().equals("1.in")) {
                sampleTestCase.setInput(content);
                isGetSampleInput=true;
            } else if(testCase.getFileName().equals("1.out")){
                sampleTestCase.setOutput(content);
                isGetSampleOutput=true;
            }
        }

        return sampleTestCase;
    }
}
