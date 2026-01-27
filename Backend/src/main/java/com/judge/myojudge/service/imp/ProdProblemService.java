package com.judge.myojudge.service.imp;

import com.cloudinary.Cloudinary;
import com.judge.myojudge.exception.ProblemNotFoundException;
import com.judge.myojudge.exception.TestCaseNotFoundException;
import com.judge.myojudge.exception.UserNotFoundException;
import com.judge.myojudge.jwt.JwtUtil;
import com.judge.myojudge.model.dto.ProblemResponse;
import com.judge.myojudge.model.dto.ProblemSampleTcResponse;
import com.judge.myojudge.model.entity.Problem;
import com.judge.myojudge.model.entity.TestCase;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.model.mapper.ProblemMapper;
import com.judge.myojudge.repository.ProblemRepo;
import com.judge.myojudge.repository.SubmissionRepo;
import com.judge.myojudge.repository.TestCaseRepo;
import com.judge.myojudge.repository.UserRepo;
import com.judge.myojudge.service.CloudinaryService;
import com.judge.myojudge.service.ProblemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Profile("prod")
public class ProdProblemService implements ProblemService {

    private final ProblemRepo problemRepo;
    private final TestCaseRepo testCaseRepo;
    private final Cloudinary cloudinary;
    private final CloudinaryService cloudinaryService;
    private final UserRepo userRepo;
    private final SubmissionRepo submissionRepo;
    private final ProblemMapper problemMapper;
    private final JwtUtil jwtUtil;


    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void saveProblem(String title,
                            String handle,
                            String difficulty,
                            String type,
                            Long coin,
                            double timeLimit,
                            double memoryLimit,
                            String problemStatement,
                            String explanation
    ){
        String mobileOrEmail= SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByMobileOrEmail(mobileOrEmail).orElseThrow(() -> new UserNotFoundException("User not found"));
        Problem problem = problemMapper.toProblem(title,handle,difficulty,type,coin,timeLimit,
                memoryLimit,problemStatement,explanation
        );
        problem.setUser(user);
        user.getProblems().add(problem);
        problemRepo.save(problem);
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
    @Transactional
    public void deleteEachProblem() {
        List<Problem> problems = problemRepo.findAll();
        if (problems.isEmpty()) {
            throw new ProblemNotFoundException("There is no problem..!");
        }
        for (Problem problem : problems) {
            for (TestCase testCase : problem.getTestcases()) {
                cloudinaryService.deleteCloudinaryFile(testCase.getFileKey(),"raw");
            }
        }
        problemRepo.deleteAll();
    }

    @Override
    @Transactional
    public void deleteProblemByHandle(String handle) {
        Optional<Problem> problem = problemRepo.findByHandleName(handle);
        if (problem.isEmpty()) {
            throw new ProblemNotFoundException("Problem Not Found Handle By: " + handle);
        }
        for (TestCase testCase : problem.get().getTestcases()) {
            cloudinaryService.deleteCloudinaryFile(testCase.getFileKey(),"raw");
        }
        problemRepo.delete(problem.get());
    }


    @Override
    @Transactional
    public ProblemSampleTcResponse getProblemPerPageById(Long problemId, String mobileOrEmail, HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        Problem problem = null;
        Boolean is_solved = false;
        if(token != null && token.startsWith("Bearer ") && token.length() > 7){
//            Long start=System.currentTimeMillis();
            var  objects = problemRepo.findProblemByStatus(problemId,mobileOrEmail);
//            Long end=System.currentTimeMillis();
//            System.out.println("]]Query Time: "+ (end-start));
            Object[] row=objects.getFirst();
            problem = (Problem) row[0];
            is_solved= (Boolean) row[1];
        }else{
            problem = problemRepo.findById(problemId)
                    .orElseThrow(() -> new ProblemNotFoundException("Problem not found"));
        }

        TestCase sampleTestcase = getSampleInput(problem.getTestcases());
        TestCase sampleOutput = getSampleOutput(problem.getTestcases());
        if(sampleTestcase == null || sampleOutput==null){
            throw new TestCaseNotFoundException("Sample testcase not found");
        }
        List<String> sampleTestcaseContent = cloudinaryService.readCloudinaryFile(sampleTestcase.getFilePath());
        List<String> sampleOutputContent = cloudinaryService.readCloudinaryFile(sampleOutput.getFilePath());
        ProblemSampleTcResponse problemSampleTcResponse = problemMapper.toProblemSampleTestCaseResponse(problem);
        problemSampleTcResponse.setSolved(is_solved);
        problemSampleTcResponse.setSampleTestcase(sampleTestcaseContent);
        problemSampleTcResponse.setSampleOutput(sampleOutputContent);
        return problemSampleTcResponse;
    }

    public Problem getProblemByID(long id) {
        Optional<Problem> problem = problemRepo.findById(id);
        if (problem.isEmpty()) {
            throw new ProblemNotFoundException("Problem not found with ID: " + id);
        }
        return problem.get();
    }

    @Override
    @Transactional
    public void saveProblemWithId(long id, String title, String handle, String difficulty, String type,
                                  String problemStatement,String explanation ,Long coins,double timeLimit,double memoryLimit, List<MultipartFile> multipartFiles) throws IOException {
        Problem problem = problemRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found with ID: " + id));
        //Problem Info Reset
        resetProblemInfo(problem,title,handle,difficulty,type,coins,
                timeLimit,memoryLimit,problemStatement,explanation
        );
        Map<String,TestCase> oldTestCases= getOlderTestCase(problem);
        List<TestCase> needDeleteTestCases=new ArrayList<>();
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            for (MultipartFile testCaseFile : multipartFiles) {
                if (!testCaseFile.isEmpty()) {
                    String fileName = testCaseFile.getOriginalFilename();
                    if(oldTestCases.containsKey(fileName)){
                        problem.getTestcases().remove(oldTestCases.get(fileName));
                        needDeleteTestCases.add(oldTestCases.get(fileName));
                    }
                    Map uploadResult = cloudinaryService.uploadTestcase(testCaseFile);
                    TestCase testCase = TestCase.builder()
                            .fileName(testCaseFile.getOriginalFilename())
                            .fileKey(uploadResult.get("public_id").toString())
                            .filePath(uploadResult.get("secure_url").toString())
                            .handle(handle)
                            .problem(problem)
                            .build();

                    if(problem.getTestcases()==null)problem.setTestcases(new ArrayList<>(Collections.singleton(testCase)));
                    else problem.getTestcases().add(testCase);
                }
            }
        }
        if(!needDeleteTestCases.isEmpty()){
            for(TestCase testCase:needDeleteTestCases){
                cloudinaryService.deleteCloudinaryFile(testCase.getFileKey(),"raw");
                testCaseRepo.delete(testCase);
            }
        }
        problemRepo.save(problem);
    }

    @Transactional(value = Transactional.TxType.REQUIRED)
    protected Map<String, TestCase> getOlderTestCase(Problem problem) {
        Map<String,TestCase> oldTestCases= new HashMap<>();
        for(TestCase testCase:problem.getTestcases()){
            oldTestCases.put(testCase.getFileName(),testCase);
        }
        return oldTestCases;
    }

    @Transactional(value = Transactional.TxType.REQUIRED)
    protected void resetProblemInfo(Problem problem, String title, String handle, String difficulty,
                                 String type, Long coins, double timeLimit,
                                 double memoryLimit, String problemStatement,
                                 String explanation) {
        problem.setTitle(title);
        problem.setHandleName(handle);
        problem.setDifficulty(difficulty);
        problem.setType(type);
        problem.setCoins(coins);
        problem.setTimeLimit(timeLimit);
        problem.setMemoryLimit(memoryLimit);
        problem.setProblemStatement(problemStatement);
        problem.setExplanation(explanation);

    }

    @Override
    @Transactional
    public Page<ProblemResponse> findProblemsByCategory(
            HttpServletRequest request,String mobileOrEmail,
            String category, String search, String difficulty,
            String solvedFilter, Pageable pageable
    ){

        String token = request.getHeader("Authorization");
        Page<?> dbResult = null;
        List<Problem> problems= null;
        List<String> userSolved= null;
        if(token!=null && !token.substring(7).isEmpty()){
            dbResult = problemRepo.findByCategoryWithSolvedOrNotFilter(
                    mobileOrEmail.trim(),
                    category.trim().toLowerCase(),
                    search.trim().toLowerCase(),
                    difficulty.trim().toLowerCase(),
                    solvedFilter.trim().toLowerCase(),
                    pageable
            );
            problems = (List<Problem>) dbResult.getContent()
                    .stream()
                    .map(row -> {
                        Object[] arr = (Object[]) row;
                        return (Problem) arr[0];
                    })
                    .toList();
            userSolved = dbResult.getContent()
                    .stream()
                    .map(row -> {
                        Object[] arr = (Object[]) row;
                        return (String) arr[1];
                    })
                    .toList();

        }
        else{
            dbResult = problemRepo.findByCategoryWithFilter(category.trim().toLowerCase(),
                    search.trim().toLowerCase(),
                    difficulty.trim().toLowerCase(),
                    solvedFilter.trim().toLowerCase(),
                    pageable
            );
            problems = (List<Problem>) dbResult.getContent();
        }
        List<ProblemResponse> problemResponses = new ArrayList<>();
        for (int i=0;i<problems.size();i++) {
            ProblemResponse tempProblemResponse = problemMapper.toProblemResponse(problems.get(i));
            if(userSolved!=null && !userSolved.isEmpty()) tempProblemResponse.setSolved(userSolved.get(i).equals("solved"));
            problemResponses.add(tempProblemResponse);
        }
        return new PageImpl<>(problemResponses, pageable, dbResult.getTotalElements());
    }

    private TestCase getSampleOutput(List<TestCase> testcases) {
        for (TestCase testCase : testcases) {
         if (testCase.getFileName().equals("1.out")) {
                return testCase;
            }
        }
        return null;
    }

    private TestCase getSampleInput(List<TestCase> testcases) {
        for (TestCase testCase : testcases) {
            if (testCase.getFileName().equals("1.in")) {
                return testCase;
            }
        }
        return null;
    }


    @Override
    public List<ProblemSampleTcResponse> findProblemAll() {
        List<ProblemSampleTcResponse> problemList = new ArrayList<>();
        List<Problem> problems = problemRepo.findAll();
        for (Problem problem : problems) {
            TestCase sampleTestcase = getSampleInput(problem.getTestcases());
            TestCase sampleOutput = getSampleOutput(problem.getTestcases());
            if(sampleTestcase == null || sampleOutput==null){
                throw new TestCaseNotFoundException("Sample testcase not found");
            }
            List<String> sampleTestcaseContent = cloudinaryService.readCloudinaryFile(sampleTestcase.getFilePath());
            List<String> sampleOutputContent = cloudinaryService.readCloudinaryFile(sampleOutput.getFilePath());
            ProblemSampleTcResponse problemSampleTcResponse = problemMapper.toProblemSampleTestCaseResponse(problem);
            problemSampleTcResponse.setSampleTestcase(sampleTestcaseContent);
            problemSampleTcResponse.setSampleOutput(sampleOutputContent);
            problemList.add(problemSampleTcResponse);
        }
        return problemList;
    }


}


