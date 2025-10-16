package com.judge.myojudge.service.imp;

import com.judge.myojudge.config.Judge0Config;
import com.judge.myojudge.exception.ProblemNotFoundException;
import com.judge.myojudge.model.dto.ExecuteTestCase;
import com.judge.myojudge.model.dto.SubmissionRequest;
import com.judge.myojudge.model.dto.SubmissionResponse;
import com.judge.myojudge.model.dto.TestcaseResultDTO;
import com.judge.myojudge.model.entity.Problem;
import com.judge.myojudge.model.entity.Submission;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.model.mapper.SubmissionMapper;
import com.judge.myojudge.repository.ProblemRepo;
import com.judge.myojudge.repository.SubmissionRepo;
import com.judge.myojudge.repository.UserRepo;
import com.judge.myojudge.service.SubmissionQueryService;
import com.judge.myojudge.service.SubmissionService;
import com.judge.myojudge.service.TestCaseService;
import jakarta.transaction.Transactional;
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
    private final SubmissionQueryService submissionQueryService;

    @Override
    @Transactional
    public SubmissionResponse runSubmissionCode(SubmissionRequest req) {
        User user= userRepo.findByMobileNumber(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new RuntimeException("User Not Found"));
        Problem problem = problemRepo.findById(req.getProblemId()).orElseThrow(() -> new ProblemNotFoundException("Problem Not Found With ID: " + req.getProblemId()));
        List<ExecuteTestCase> testcases = testCaseService.getTestCaseWithFile(req.getProblemId());
        Integer languageId = mapLanguageToId(req.getLanguage());

        List<TestcaseResultDTO> results = new ArrayList<>();

        for (ExecuteTestCase testcase : testcases) {
            results.add(executeSingleTestcase(req.getSubmissionCode(), languageId, testcase));
        }

        float maxTimeTake=0; long maxSpaceTake=0;
        String verdict=""; boolean flag=true;
        List<TestcaseResultDTO> passedTestcases=new ArrayList<>();

        for(TestcaseResultDTO result:results){

            maxTimeTake=Math.max(maxTimeTake, Float.parseFloat(result.getTime()==null?"0":result.getTime()));
            maxSpaceTake=Math.max(maxSpaceTake, Integer.parseInt(result.getMemory()==null?"0":result.getMemory()));

            if(flag && result.getStatus().equalsIgnoreCase("Accepted")){
                verdict="Accepted";
                passedTestcases.add(result);
            }
            else if(flag && result.getStatus().equalsIgnoreCase("Wrong Answer")){
                verdict="Wrong Answer";
                flag=false;
                user.setTotalProblemsAttempted(user.getTotalProblemsAttempted()==null?1:user.getTotalProblemsAttempted()+1);
                user.setTotalProblemsFailed(user.getTotalProblemsSolved()==null?0:user.getTotalProblemsSolved()+1);
                user.setTotalProblemsWA(user.getTotalProblemsWA()==null?1:user.getTotalProblemsWA()+1);
                passedTestcases.add(result);
                break;
            }
            else if(flag && result.getStatus().equalsIgnoreCase("Runtime Error")){
                verdict="Runtime Error";
                flag=false;
                passedTestcases.add(result);
                user.setTotalProblemsAttempted(user.getTotalProblemsAttempted()==null?1:user.getTotalProblemsAttempted()+1);
                user.setTotalProblemsFailed(user.getTotalProblemsSolved()==null?0:user.getTotalProblemsSolved()+1);
                user.setTotalProblemsRE(user.getTotalProblemsRE()==null?1:user.getTotalProblemsRE()+1);
                break;
            }
            else if (flag && result.getStatus().equalsIgnoreCase("Compilation Error")) {
                verdict="Compilation Error";
                flag=false;
                user.setTotalProblemsAttempted(user.getTotalProblemsAttempted()==null?1:user.getTotalProblemsAttempted()+1);
                user.setTotalProblemsFailed(user.getTotalProblemsSolved()==null?0:user.getTotalProblemsSolved()+1);
                user.setTotalProblemsCE(user.getTotalProblemsCE()==null?1:user.getTotalProblemsCE()+1);
                break;
            }
            else if (flag && result.getStatus().equalsIgnoreCase("Time Limit Exceeded")) {
                verdict="Time Limit Exceeded";
                flag=false;
                user.setTotalProblemsAttempted(user.getTotalProblemsAttempted()==null?1:user.getTotalProblemsAttempted()+1);
                user.setTotalProblemsFailed(user.getTotalProblemsSolved()==null?0:user.getTotalProblemsSolved()+1);
                user.setTotalProblemsTLE(user.getTotalProblemsTLE()==null?1:user.getTotalProblemsTLE()+1);
                break;
            }
        }

        if(verdict.equalsIgnoreCase("Accepted")){
            user.setTotalProblemsSolved(user.getTotalProblemsSolved()==null?1:user.getTotalProblemsSolved()+1);
            user.setTotalProblemsAttempted(user.getTotalProblemsAttempted()==null?0:user.getTotalProblemsAttempted()-1);
        }
        SubmissionResponse response = new SubmissionResponse();
        response.setResults(passedTestcases);
        response.setTotal(results.size());

        if(!flag && verdict.equals("Compilation Error"))response.setPassed(0);
        if(!flag && !verdict.equals("Compilation Error"))response.setPassed(passedTestcases.size()-1);
        else response.setPassed(passedTestcases.size());

        response.setVerdict(verdict);
        response.setTime(maxTimeTake);
        response.setMemory(maxSpaceTake);
        response.setProblemName(problem.getTitle());
        Submission submission= Submission.builder()
                .language(req.getLanguage())
                .userCode(req.getSubmissionCode().trim())
                .status(verdict)
                .memory((long) maxSpaceTake)
                .handle(problem.getHandleName())
                .time(maxTimeTake)
                .totalTestcases(response.getTotal())
                .passedTestcases(response.getPassed())
                .coinsEarned(problem.getCoins())
                .problem(problem)
                .user(user)
                .build();
        problem.getSubmissions().add(submission);
        user.getSubmissions().add(submission);
        List<Submission> coinFlag= submissionQueryService.getSubmissionsByUserWithAccepted(user.getMobileNumber(),problem.getHandleName(),"Accepted");
        if(coinFlag.isEmpty()){
            user.setTotalCoinsEarned((user.getTotalCoinsEarned()==null?0: user.getTotalCoinsEarned())+problem.getCoins());
            user.setTotalPresentCoins((user.getTotalPresentCoins()==null?0:user.getTotalPresentCoins())+problem.getCoins());
        }else{
            user.setTotalCoinsEarned((user.getTotalCoinsEarned()==null?0: user.getTotalCoinsEarned()));
            user.setTotalPresentCoins((user.getTotalPresentCoins()==null?0:user.getTotalPresentCoins()));
        }

        submissionRepo.save(submission);
        return response;
    }

    @Override
    public SubmissionResponse runSampleTestCaseCode(SubmissionRequest req) {
        User user= userRepo.findByMobileNumber(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new RuntimeException("User Not Found"));
        Problem problem = problemRepo.findById(req.getProblemId()).orElseThrow(() -> new ProblemNotFoundException("Problem Not Found With ID: " + req.getProblemId()));
        ExecuteTestCase testcase = testCaseService.getSampleTestCaseWithFile(req.getProblemId());
        Integer languageId = mapLanguageToId(req.getLanguage());
        TestcaseResultDTO result = executeSingleTestcase(req.getSubmissionCode(), languageId, testcase);
        SubmissionResponse response = new SubmissionResponse();
        response.setResults(Collections.singletonList(result));
        response.setTotal(1);
        response.setPassed(0);
        response.setVerdict(result.getStatus());
        response.setTime(Float.parseFloat(result.getTime()==null?"0":result.getTime()));
        response.setMemory(Long.parseLong(result.getMemory()==null?"0":result.getMemory()));
        return response;
    }

    @Override
    @Transactional
    public Page<SubmissionResponse> getAllSubmissionByUser(String contact, String search, Sort sort, int page, int size) {
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<SubmissionResponse> submissionResponses = submissionRepo.findSubmissionsByContact(contact,search,pageable).map(submissionMapper::toSubmissionResponse);
        return submissionResponses;
    }

    public TestcaseResultDTO executeSingleTestcase(String code, Integer languageId, ExecuteTestCase testcase) {
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
//        System.out.println(tr);
        return tr;
    }

    private String decodeBase64(String base64) {
        if (base64 == null) return null;
        return new String(Base64.getDecoder().decode(base64));
    }

    private Integer mapLanguageToId(String language) {
        return switch (language.toLowerCase()) {
            case "python3" -> 19;
            case "java" -> 13;
            case "cpp" -> 7;
            case "c" -> 2;
            case "javascript" -> 63;
            default -> null;
        };
    }
//    private Integer mapLanguageToId(String language) {
//        return switch (language.toLowerCase()) {
//            case "python3" -> 71;
//            case "java" -> 62;
//            case "cpp" -> 7;
//            case "c" -> 50;
//            case "javascript" -> 63;
//            default -> null;
//        };
//    }
}
