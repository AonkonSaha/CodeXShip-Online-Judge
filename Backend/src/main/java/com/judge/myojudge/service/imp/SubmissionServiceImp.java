package com.judge.myojudge.service.imp;

import com.judge.myojudge.config.Judge0Config;
import com.judge.myojudge.exception.ProblemNotFoundException;
import com.judge.myojudge.model.dto.*;
import com.judge.myojudge.model.entity.Problem;
import com.judge.myojudge.model.entity.Submission;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.model.mapper.SubmissionMapper;
import com.judge.myojudge.repository.ProblemRepo;
import com.judge.myojudge.repository.SubmissionRepo;
import com.judge.myojudge.repository.UserRepo;
import com.judge.myojudge.service.SubmissionService;
import com.judge.myojudge.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRepo userRepo;
    private final SubmissionRepo submissionRepo;
    private final SubmissionMapper submissionMapper;

    @Override
    public SubmissionResponse excuteCode(SubmissionRequest req) {
        User user= userRepo.findByMobileNumber(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new RuntimeException("User Not Found"));
        Problem problem = problemRepo.findById(req.getProblemId()).orElseThrow(() -> new ProblemNotFoundException("Problem Not Found With ID: " + req.getProblemId()));
        List<ExecuteTestCase> testcases = testCaseService.getTestCaseWithFile(req.getProblemId());
        Integer languageId = mapLanguageToId(req.getLanguage());

        List<TestcaseResultDTO> results = new ArrayList<>();

        for (ExecuteTestCase testcase : testcases) {
            results.add(executeSingleTestcase(req.getSubmissionCode(), languageId, testcase));
        }

        SubmissionResponse response = new SubmissionResponse();
        response.setResults(results);
        response.setTotal(results.size());
        response.setPassed((int) results.stream().filter(TestcaseResultDTO::isPassed).count());

        float maxTimeTake=0; long maxSpaceTake=0;

        String verdict=""; boolean flag=true;

        for(TestcaseResultDTO result:results){

            maxTimeTake=Math.max(maxTimeTake, Float.parseFloat(result.getTime()==null?"0":result.getTime()));
            maxSpaceTake=Math.max(maxSpaceTake, Integer.parseInt(result.getMemory()==null?"0":result.getMemory()));

            if(flag && result.getStatus().equalsIgnoreCase("Accepted")){
                verdict="Accepted";
            }
            else if(flag && result.getStatus().equalsIgnoreCase("Wrong Answer")){
                verdict="Wrong Answer";
                flag=false;
            }
            else if(flag && result.getStatus().equalsIgnoreCase("Runtime Error")){
                verdict="Runtime Error";
                flag=false;
            }
            else if (flag && result.getStatus().equalsIgnoreCase("Compilation Error")) {
                verdict="Compilation Error";
                flag=false;
            }
            else if (flag && result.getStatus().equalsIgnoreCase("Time Limit Exceeded")) {
                verdict="Time Limit Exceeded";
                flag=false;
            }
        }
        response.setVerdict(verdict);
        response.setTime(maxTimeTake);
        response.setMemory(maxSpaceTake);

        Submission submission= Submission.builder()
                .language(req.getLanguage())
                .userCode(req.getSubmissionCode().trim())
                .status(verdict)
                .memory((long) maxSpaceTake)
                .time(maxTimeTake)
                .totalTestcases(response.getTotal())
                .passedTestcases(response.getPassed())
                .problem(problem)
                .user(user)
                .build();
        problem.getSubmissions().add(submission);
        user.getSubmissions().add(submission);
        submissionRepo.save(submission);
        return response;
    }

    @Override
    public Page<SubmissionResponse> getAllSubmissionByUser(String contact, String search, Sort sort, int page, int size) {
        Pageable pageable = PageRequest.of(page,size,sort);
        return submissionRepo.findSubmissionsByContact(contact,search,pageable).map(submissionMapper::toSubmissionResponse);
    }

    private TestcaseResultDTO executeSingleTestcase(String code, Integer languageId, ExecuteTestCase testcase) {

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
        TestcaseResultDTO tr = new TestcaseResultDTO();
        tr.setStatus(status != null ? status.get("description").toString() : "Unknown");
        tr.setStdout(decodeBase64((String) jr.get("stdout")));
        tr.setExpectedOutput(testcase.getOutput());
        tr.setStderr(decodeBase64((String) jr.get("stderr")));
        tr.setCompileOutput(decodeBase64((String) jr.get("compile_output")));
        tr.setMessage((String) jr.get("message"));
        tr.setTime(jr.get("time") != null ? jr.get("time").toString() : null);
        tr.setMemory(jr.get("memory") != null ? jr.get("memory").toString() : null);


        boolean passed = tr.getStdout() != null &&
                testcase.getOutput() != null &&
                tr.getStdout().trim().equals(testcase.getOutput().trim()) &&
                "Accepted".equalsIgnoreCase(tr.getStatus());
        tr.setPassed(passed);
        System.out.println(tr);
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
