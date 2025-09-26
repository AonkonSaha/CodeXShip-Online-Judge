package com.judge.myojudge.service.imp;

import com.judge.myojudge.config.Judge0Config;
import com.judge.myojudge.exception.ProblemNotFoundException;
import com.judge.myojudge.model.dto.*;
import com.judge.myojudge.repository.ProblemRepo;
import com.judge.myojudge.service.SubmissionService;
import com.judge.myojudge.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImp implements SubmissionService {

    private final Judge0Config config;
    private final TestCaseService testCaseService;
    private final ProblemRepo problemRepo;
    private final WebClient webClient;

    @Override
    public SubmissionResponse excuteCode(SubmissionRequest req) {

        List<ExecuteTestCase> testcases = testCaseService.getTestCaseWithFile(req.getProblemId());
        if (testcases.isEmpty()) {
            throw new ProblemNotFoundException("TestCase Not Found With ID: " + req.getProblemId());
        }

        Integer languageId = mapLanguageToId(req.getLanguage());
        if (languageId == null) {
            throw new ProblemNotFoundException("Unsupported language: " + req.getLanguage());
        }

        if (req.getSubmissionCode() == null || req.getSubmissionCode().trim().isEmpty()) {
            throw new ProblemNotFoundException("Submission code cannot be empty!");
        }

        List<TestcaseResult> results = new ArrayList<>();

        for (ExecuteTestCase testcase : testcases) {
            results.add(executeSingleTestcase(req.getSubmissionCode(), languageId, testcase));
        }

        SubmissionResponse response = new SubmissionResponse();
        response.setResults(results);
        response.setTotal(results.size());
        response.setPassed((int) results.stream().filter(TestcaseResult::isPassed).count());

        return response;
    }

    private TestcaseResult executeSingleTestcase(String code, Integer languageId, ExecuteTestCase testcase) {

        String encodedCode = Base64.getEncoder().encodeToString(code.trim().getBytes());
        String encodedInput = Base64.getEncoder().encodeToString(testcase.getInput().getBytes());
        String encodedOutput = Base64.getEncoder().encodeToString(testcase.getOutput().getBytes());

        Map<String, Object> submission = new HashMap<>();
        submission.put("language_id", languageId);
        submission.put("source_code", encodedCode);
        submission.put("stdin", encodedInput);
        submission.put("expected_output", encodedOutput);
        submission.put("wait", true);

        Map<String, Object> jr;
        try {
            jr = webClient.post()
                    .uri("/submissions?base64_encoded=true&wait=true")
                    .bodyValue(submission)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Error submitting code to Judge0: " + e.getResponseBodyAsString(), e);
        }

        if (jr == null) throw new RuntimeException("Judge0 returned empty response");

        Map<String, Object> status = (Map<String, Object>) jr.get("status");
        TestcaseResult tr = new TestcaseResult();
        tr.setStatus(status != null ? status.get("description").toString() : "Unknown");
        tr.setStdout(decodeBase64((String) jr.get("stdout")));
        tr.setExpectedOutput(testcase.getOutput());
        tr.setStderr(decodeBase64((String) jr.get("stderr")));
        tr.setCompileOutput(decodeBase64((String) jr.get("compile_output")));
        tr.setMessage((String) jr.get("message"));
        tr.setTime(jr.get("time") != null ? jr.get("time").toString() : null);
        tr.setMemory(jr.get("memory") != null ? jr.get("memory").toString() : null);

        System.out.println(tr);
        boolean passed = tr.getStdout() != null &&
                testcase.getOutput() != null &&
                tr.getStdout().trim().equals(testcase.getOutput().trim()) &&
                "Accepted".equalsIgnoreCase(tr.getStatus());
        tr.setPassed(passed);

        return tr;
    }

    private String decodeBase64(String base64) {
        if (base64 == null) return null;
        return new String(Base64.getDecoder().decode(base64));
    }

    private Integer mapLanguageToId(String language) {
        return switch (language.toLowerCase()) {
            case "python3" -> 71;
            case "java" -> 62;
            case "cpp" -> 52;
            case "c" -> 50;
            case "javascript" -> 63;
            default -> null;
        };
    }
}
