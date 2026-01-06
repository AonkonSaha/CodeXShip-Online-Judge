package com.judge.myojudge.service.imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.judge.myojudge.config.Judge0Config;
import com.judge.myojudge.exception.ProblemNotFoundException;
import com.judge.myojudge.exception.UserNotFoundException;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

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
    private final RestClient restClient;
    private final ExecutorService executorService;
    private final PlatformTransactionManager platformTransactionManager;
    private final SimpMessagingTemplate messagingTemplate;



    @Override
    public Submission getSubmission() {
        System.out.println("Get Submission Function Thread Name: "+Thread.currentThread().getName());
        Submission submission = new Submission();
        submission.setCreatedAt(LocalDateTime.now());
        submissionRepo.save(submission);
        return submission;
    }
    @CacheEvict(cacheNames = {"problems","userDetails","CoinWithImg","users","user"}, allEntries = true)
    public void runSubmissionCode(SubmissionRequest req,
                                  Submission submission,
                                  String mobileOrEmail) throws ExecutionException, InterruptedException {
        System.out.println("Run Submission Function Thread Name: "+Thread.currentThread().getName());
        List<ExecuteTestCase> testcases = testCaseService.getTestCaseWithFile(req.getProblemId());
        Integer languageId = mapLanguageToId(req.getLanguage());
        executorService.submit(()-> {
            TransactionTemplate tx = new TransactionTemplate(platformTransactionManager);

            tx.execute( status -> {
                try {
                    judgingTestCases(
                            req,
                            submission.getId(),
                            mobileOrEmail,
                            languageId,
                            testcases,
                            testcases.size()
                    );
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return null;
            });
        });
    }

    private void judgingTestCases(SubmissionRequest req,
                                                     Long submissionId,
                                                     String mobileOrEmail,
                                                     Integer languageId,
                                                     List<ExecuteTestCase> testcases,
                                                     int totalTestcases) throws InterruptedException {
        System.out.println("Judging Testcases Function Thread Name: "+Thread.currentThread().getName());
        int index=0;
        List<TestcaseResultDTO> results = new ArrayList<>();
        for (ExecuteTestCase testcase : testcases) {
            index++;
            TestcaseResultDTO testcaseResultDTO = executeSingleTestcase(req.getSubmissionCode(), languageId, testcase);
            messagingTemplate.convertAndSend(
                    "/topic/submission/" + submissionId,
                    testcaseResultDTO
            );
            results.add(testcaseResultDTO);
            if(!testcaseResultDTO.isPassed())break;        }
        saveSubmission(req,results,submissionId,mobileOrEmail,totalTestcases);
    }

    public SubmissionResponse saveSubmission(SubmissionRequest req,
                                             List<TestcaseResultDTO> results,
                                             Long submissionId,
                                             String mobileOrEmail,
                                             int totalTestcases) {
        System.out.println("Save Submission Function Thread Name: "+Thread.currentThread().getName());
        Submission submission =submissionRepo.findById(submissionId).orElseThrow(()->new RuntimeException("Submission Not Found: "+submissionId));
        Problem problem = problemRepo.findById(req.getProblemId()).orElseThrow(() -> new ProblemNotFoundException("Problem Not Found With ID: " + req.getProblemId()));
        User user = userRepo.findByMobileOrEmail(mobileOrEmail).orElseThrow(() -> new UserNotFoundException(mobileOrEmail));
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
                user.setTotalProblemsFailed(user.getTotalProblemsSolved()==null?0:user.getTotalProblemsSolved()+1);
                user.setTotalProblemsWA(user.getTotalProblemsWA()==null?1:user.getTotalProblemsWA()+1);
                passedTestcases.add(result);
                break;
            }
            else if(flag && result.getStatus().equalsIgnoreCase("Runtime Error (NZEC)")){
                verdict="Runtime Error";
                flag=false;
                passedTestcases.add(result);
                user.setTotalProblemsFailed(user.getTotalProblemsSolved()==null?0:user.getTotalProblemsSolved()+1);
                user.setTotalProblemsRE(user.getTotalProblemsRE()==null?1:user.getTotalProblemsRE()+1);
                break;
            }
            else if (flag && result.getStatus().equalsIgnoreCase("Compilation Error")) {
                verdict="Compilation Error";
                flag=false;
                user.setTotalProblemsFailed(user.getTotalProblemsSolved()==null?0:user.getTotalProblemsSolved()+1);
                user.setTotalProblemsCE(user.getTotalProblemsCE()==null?1:user.getTotalProblemsCE()+1);
                break;
            }
            else if (flag && result.getStatus().equalsIgnoreCase("Time Limit Exceeded")) {
                verdict="Time Limit Exceeded";
                flag=false;
                user.setTotalProblemsFailed(user.getTotalProblemsSolved()==null?0:user.getTotalProblemsSolved()+1);
                user.setTotalProblemsTLE(user.getTotalProblemsTLE()==null?1:user.getTotalProblemsTLE()+1);
                break;
            }
        }
        SubmissionResponse response = setSubmissionResponseData(
                passedTestcases,
                results,
                problem,
                submission,
                verdict,
                maxSpaceTake,
                maxTimeTake,
                flag
        );
        setSubmissionData(
                user,
                req,
                problem,
                submission,
                response,
                verdict,
                maxSpaceTake,
                maxTimeTake
        );
        problem.getSubmissions().add(submission);
        user.getSubmissions().add(submission);
        List<Submission> coinFlag= submissionQueryService.getSubmissionsByUserWithAccepted(mobileOrEmail,problem.getHandleName(),"Accepted");
        if(coinFlag.isEmpty() && flag){
            if(verdict.equalsIgnoreCase("Accepted")){
                user.setTotalProblemsSolved(user.getTotalProblemsSolved()==null?1:user.getTotalProblemsSolved()+1);
                user.setTotalProblemsAttempted(user.getTotalProblemsAttempted()==null?1:user.getTotalProblemsAttempted()+1);
            }
            response.setCoins(problem.getCoins());
            user.setTotalCoinsEarned((user.getTotalCoinsEarned()==null?0: user.getTotalCoinsEarned())+problem.getCoins());
            user.setTotalPresentCoins((user.getTotalPresentCoins()==null?0:user.getTotalPresentCoins())+problem.getCoins());
        }else{
            if(!flag){
                user.setTotalProblemsAttempted(user.getTotalProblemsAttempted()==null?1:user.getTotalProblemsAttempted()+1);
            }
            response.setCoins(0L);
            user.setTotalCoinsEarned((user.getTotalCoinsEarned()==null?0: user.getTotalCoinsEarned()));
            user.setTotalPresentCoins((user.getTotalPresentCoins()==null?0:user.getTotalPresentCoins()));
        }
        response.setCompleted(true);
        submissionRepo.save(submission);
        messagingTemplate.convertAndSend(
                "/topic/submission/" + submission.getId(),
                response
        );
        System.out.println("Submission Details: "+submission);
        return response;
    }

    private SubmissionResponse setSubmissionResponseData(List<TestcaseResultDTO> passedTestcases,
                                                         List<TestcaseResultDTO> results,
                                                         Problem problem,
                                                         Submission submission,
                                                         String verdict,
                                                         Long maxSpaceTake,
                                                         Float maxTimeTake,
                                                         Boolean flag
    ) {
        SubmissionResponse response = SubmissionResponse.builder()
                .results(passedTestcases)
                .total(results.size())
                .verdict(verdict)
                .time(maxTimeTake)
                .memory(maxSpaceTake)
                .problemName(problem.getTitle())
                .id(submission.getId())
                .createdAt(submission.getCreatedAt())
                .build();
        if(!flag && verdict.equals("Compilation Error"))response.setPassed(0);
        if(!flag && !verdict.equals("Compilation Error"))response.setPassed(passedTestcases.size()-1);
        else response.setPassed(passedTestcases.size());
        return response;
    }

    @Transactional(value = Transactional.TxType.REQUIRED)
    public void setSubmissionData(User user,
                                  SubmissionRequest req,
                                  Problem problem,
                                  Submission submission,
                                  SubmissionResponse response,
                                  String verdict,
                                  long maxSpaceTake,
                                  float maxTimeTake) {
        submission.setLanguage(req.getLanguage());
        submission.setUserCode(req.getSubmissionCode().trim());
        submission.setStatus(verdict);
        submission.setMemory((long) maxSpaceTake);
        submission.setHandle(problem.getHandleName());
        submission.setTime(maxTimeTake);
        submission.setCreatedAt(LocalDateTime.now());
        submission.setTotalTestcases(response.getTotal());
        submission.setPassedTestcases(response.getPassed());
        submission.setCoinsEarned(problem.getCoins());
        submission.setProblem(problem);
        submission.setUser(user);
    }

    @Override
    public SubmissionResponse runSampleTestCaseCode(SubmissionRequest req) {
        String mobileOrEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = null;
        if(mobileOrEmail.contains("@")){
            user = userRepo.findByEmail(mobileOrEmail).orElseThrow(()-> new UserNotFoundException("User not found"));
        }else{
            user = userRepo.findByMobileNumber(mobileOrEmail).orElseThrow(()-> new UserNotFoundException("User not found"));
        }
        Problem problem = problemRepo.findById(req.getProblemId()).orElseThrow(() -> new ProblemNotFoundException("Problem Not Found With ID: " + req.getProblemId()));
        ExecuteTestCase testcase = testCaseService.getSampleTestCaseWithFile(req.getProblemId());
        Integer languageId = mapLanguageToId(req.getLanguage());
        TestcaseResultDTO result = executeSingleTestcase(req.getSubmissionCode(), languageId, testcase);
        SubmissionResponse response = new SubmissionResponse();
        response.setResults(List.of(result));
        response.setTotal(1);
        response.setPassed(0);
        response.setVerdict(result.getStatus());
        response.setTime(Float.parseFloat(result.getTime()==null?"0":result.getTime()));
        response.setMemory(Long.parseLong(result.getMemory()==null?"0":result.getMemory()));
        return response;
    }

    @Override
    @Transactional
    public Page<SubmissionResponse> getAllSubmissionByUser(String mobileOrEmail, String search, Sort sort, int page, int size) {
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<SubmissionResponse> submissionResponses = submissionRepo.findSubmissionsByMobileOrEmail(mobileOrEmail,search,pageable).map(submissionMapper::toSubmissionResponse);
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
        ObjectMapper objectMapper=new ObjectMapper();
        try {
            jr = restClient.post()
                    .uri("/submissions?base64_encoded=true&wait=true")
                    .body(objectMapper.writeValueAsString(submission))
                    .retrieve()
                    .body(Map.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Error submitting code to Judge0: " + e.getMessage(), e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
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
    private Map<String, Integer> getLanguages() {

        List<Map<String, Object>> response = webClient.get()
                .uri("/languages")
                .retrieve()
                .bodyToMono(List.class)
                .block();

//        if (response == null) return Collections.emptyMap();

        Map<String, Integer> languages = new HashMap<>();
        for (Map<String, Object> lang : response) {
            String name = ((String) lang.get("name")).toLowerCase();
            Integer id = (Integer) lang.get("id");
            languages.put(name, id);
        }
        return languages;
    }

    private String decodeBase64(String base64) {
        if (base64 == null) return null;
        return new String(Base64.getDecoder().decode(base64));
    }

    private Integer mapLanguageToId(String language) {
        return switch (language.toLowerCase()) {
            case "python-pypy-7.3.12-3.9" -> 27;
            case "python-pypy-7.3.12-3.10" -> 28;
            case "java-jdk-14.0.1" -> 4;
            case "cpp-clang-9.0.1-14" -> 14;
            case "cpp-clang-10.0.1-17" -> 2;
            case "c-clang-10.0.1-17" -> 1;
            case "csharp-sdk-3.1.406" -> 21;
            case "csharp-sdk-8.0.302" -> 30;
            default -> null;
        };
    }
}