package com.judge.myojudge.execution_validations_code.ev_service;

import com.judge.myojudge.execution_validations_code.ev_model.CodeSubmission;
import com.judge.myojudge.execution_validations_code.ev_model.ExecutionResult;
import com.judge.myojudge.execution_validations_code.ev_model.TestCaseResult;
import com.judge.myojudge.execution_validations_code.ev_repo.CodeSubmissionRepository;
import com.judge.myojudge.model.entity.Problem;
import com.judge.myojudge.model.entity.TestCase;
import com.judge.myojudge.repository.ProblemRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CodeExecutionService {

    private final ProblemRepo problemRepo;
    private final S3FileService s3FileService;
    private final CodeRunner codeRunner;
    private final CodeSubmissionRepository codeSubmissionRepository;

    public ExecutionResult execute(CodeSubmission submission) {
        // Retrieve the problem from the database
        Problem problem = problemRepo.findById(submission.getProblemId())
                .orElseThrow(() -> new RuntimeException("Problem not found with ID: " + submission.getProblemId()));

        // Fetch the test cases for the given problem
        List<TestCase> testCases = problem.getTestcases();

        // Maps to store input and output test case files
        Map<String, List<String>> inputCases = new HashMap<>();
        Map<String, List<String>> outputCases = new HashMap<>();

        // Fetch the test case files from S3 and categorize them
        testCases.forEach(testCase -> {
            List<String> content = s3FileService.getFileLinesFromS3(testCase.getFileKey());
            if (testCase.getFileName().endsWith(".in")) {
                inputCases.put(testCase.getFileName(), content);
            } else if (testCase.getFileName().endsWith(".out")) {
                outputCases.put(testCase.getFileName(), content);
            }
        });

        // To store the results of each test case
        List<TestCaseResult> testCaseResults = new ArrayList<>();
        boolean allPassed = true;

        // Loop through the input test cases and execute code for each
        for (Map.Entry<String, List<String>> entry : inputCases.entrySet()) {
            String inputFileName = entry.getKey();
            List<String> inputLines = entry.getValue();

            // Find the expected output for the given input
            String expectedOutputFileName = inputFileName.replace(".in", ".out");
            List<String> expectedOutputLines = outputCases.getOrDefault(expectedOutputFileName, Collections.emptyList());

            // Run the user's code with the given input lines
            String actualOutput = codeRunner.runCode(submission.getLanguage(), submission.getUserCode(), inputLines);
            List<String> actualOutputLines = Arrays.asList(actualOutput.split("\n"));
//            System.out.println("Inputttt");
//            for(String s:inputLines) System.out.println(s);
//            System.out.println(">>>>>>>>>>>>>>>>> Output");
//
//            for(String s:expectedOutputLines) System.out.println(s);
            System.out.println(">>>>>>>>>>>>>>>>>> GetOutput");

            for(String s:actualOutputLines) System.out.println(s);
            System.out.println("************************");
            // Compare the actual output with the expected output
            boolean isCorrect = compareOutputs(expectedOutputLines, actualOutputLines);
            allPassed &= isCorrect;

            // Record the result of the test case
            testCaseResults.add(new TestCaseResult(inputFileName, isCorrect));
        }

        // Set the status of the submission
        submission.setStatus(allPassed ? "Success" : "Failed");
        submission.setUserCode(""); // Clear the user's code for security reasons
        codeSubmissionRepository.save(submission);

        return new ExecutionResult(submission.getProblemId(), testCaseResults);
    }

    // Utility method to compare expected output with actual output
    private boolean compareOutputs(List<String> expectedOutputLines, List<String> actualOutputLines) {
        // Trim the lines to handle extra spaces or newlines
        return expectedOutputLines.stream()
                .map(String::trim)
                .toList()
                .equals(actualOutputLines.stream().map(String::trim).collect(Collectors.toList()));
    }
}
