package com.judge.myojudge.service.imp;

import com.cloudinary.Cloudinary;
import com.judge.myojudge.exception.ProblemNotFoundException;
import com.judge.myojudge.exception.UserNotFoundException;
import com.judge.myojudge.model.dto.ProblemDTO;
import com.judge.myojudge.model.dto.ProblemWithSample;
import com.judge.myojudge.model.entity.Problem;
import com.judge.myojudge.model.entity.TestCase;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.repository.ProblemRepo;
import com.judge.myojudge.repository.TestCaseRepo;
import com.judge.myojudge.repository.UserRepo;
import com.judge.myojudge.service.CloudinaryService;
import com.judge.myojudge.service.ProblemService;
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

    @Override
    public List<ProblemWithSample> findProblemAll() {
        List<ProblemWithSample> problemList = new ArrayList<>();
        List<Problem> problems = problemRepo.findAll();

        for (Problem problem : problems) {
            ProblemWithSample problemWithSample = new ProblemWithSample();
            problemWithSample.setId(problem.getId());
            problemWithSample.setTitle(problem.getTitle());
            problemWithSample.setHandle(problem.getHandleName());
            problemWithSample.setProblemStatement(problem.getProblemStatement());
            problemWithSample.setType(problem.getType());
            problemWithSample.setDifficulty(problem.getDifficulty());

            TestCase sampleTestcase = null;
            TestCase sampleOutput = null;

            for (TestCase testCase : problem.getTestcases()) {
                if (testCase.getFileName().equals("1.in")) {
                    sampleTestcase = testCase;
                } else if (testCase.getFileName().equals("1.out")) {
                    sampleOutput = testCase;
                }
            }

            List<String> sampleTestcaseContent = cloudinaryService.readCloudinaryFile(sampleTestcase.getFilePath());
            List<String> sampleOutputContent = cloudinaryService.readCloudinaryFile(sampleOutput.getFilePath());

            problemWithSample.setSampleTestcase(sampleTestcaseContent);
            problemWithSample.setSampleOutput(sampleOutputContent);

            problemList.add(problemWithSample);
        }
        return problemList;
    }



    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void saveProblem(String title,
                            String handle,
                            String difficulty,
                            String type,
                            Long coin,
                            String problemStatement) {
        String contact= SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByMobileNumber(contact).orElseThrow(() -> new UserNotFoundException("User not found with mobile number: " + contact));
        Problem problem = new Problem();
        problem.setTitle(title);
        problem.setHandleName(handle);
        problem.setDifficulty(difficulty);
        problem.setType(type);
        problem.setCoins(coin);
        problem.setProblemStatement(problemStatement);
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
    public ProblemWithSample findProblemByID(Long id) {
        Problem problem = problemRepo.findById(id)
                .orElseThrow(() -> new ProblemNotFoundException("Problem not found"));
        ProblemWithSample problemWithSample = new ProblemWithSample();
        problemWithSample.setId(problem.getId());
        problemWithSample.setTitle(problem.getTitle());
        problemWithSample.setProblemStatement(problem.getProblemStatement());
        problemWithSample.setDifficulty(problem.getDifficulty());
        problemWithSample.setType(problem.getType());
        problemWithSample.setHandle(problem.getHandleName());
        problemWithSample.setCoins(problem.getCoins());

        TestCase sampleTestcase = null;
        TestCase sampleOutput = null;

        for (TestCase testCase : problem.getTestcases()) {
            if (testCase.getFileName().equals("1.in")) {
                sampleTestcase = testCase;
            } else if (testCase.getFileName().equals("1.out")) {
                sampleOutput = testCase;
            }
        }

        List<String> sampleTestcaseContent = cloudinaryService.readCloudinaryFile(sampleTestcase.getFilePath());
        List<String> sampleOutputContent = cloudinaryService.readCloudinaryFile(sampleOutput.getFilePath());

        problemWithSample.setSampleTestcase(sampleTestcaseContent);
        problemWithSample.setSampleOutput(sampleOutputContent);

        return problemWithSample;
    }

    @Transactional
    public ProblemDTO fetchOneProblemByID(long id) {
        Optional<Problem> problem = problemRepo.findById(id);
        if (problem.isEmpty()) {
            throw new ProblemNotFoundException("Problem not found with ID: " + id);
        }
        Map<String,String> testCaseNameWithPath=new HashMap<>();
        ProblemDTO problemDTO = new ProblemDTO();
        problemDTO.setTitle(problem.get().getTitle());
        problemDTO.setDifficulty(problem.get().getDifficulty());
        problemDTO.setType(problem.get().getType());
        problemDTO.setHandle(problem.get().getHandleName());
        problemDTO.setCoins(problem.get().getCoins());
        problemDTO.setProblemStatement(problem.get().getProblemStatement());
        for(TestCase testCase:problem.get().getTestcases()){
            testCaseNameWithPath.put(testCase.getFileName(),testCase.getFilePath());
        }
        problemDTO.setTestCaseNameWithPath(testCaseNameWithPath);
        return problemDTO;
    }

    @Override
    @Transactional
    public void saveProblemWithId(long id, String title, String handle, String difficulty, String type,
                                  String problemStatement,Long coins, List<MultipartFile> multipartFiles) throws IOException {
        Problem problem = problemRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found with ID: " + id));
        problem.setTitle(title);
        problem.setHandleName(handle);
        problem.setDifficulty(difficulty);
        problem.setType(type);
        problem.setCoins(coins);
        problem.setProblemStatement(problemStatement);
        List<TestCase> needDeleteTestCases=new ArrayList<>();
        Map<String,TestCase> oldTestCases=new HashMap<>();
        for(TestCase testCase:problem.getTestcases()){
            oldTestCases.put(testCase.getFileName(),testCase);
        }
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            for (MultipartFile testCaseFile : multipartFiles) {
                if (!testCaseFile.isEmpty()) {
                    String fileName = testCaseFile.getOriginalFilename();
                    System.out.println("FileName: "+fileName);
                    if(oldTestCases.containsKey(fileName)){
                        problem.getTestcases().remove(oldTestCases.get(fileName));
                        needDeleteTestCases.add(oldTestCases.get(fileName));
                    }
                    Map uploadResult = cloudinaryService.uploadTestcase(testCaseFile);
                    TestCase testCase = new TestCase();
                    testCase.setFileName(testCaseFile.getOriginalFilename());
                    testCase.setFileKey(uploadResult.get("public_id").toString());
                    testCase.setFilePath(uploadResult.get("secure_url").toString());
                    testCase.setHandle(handle);
                    testCase.setProblem(problem);
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

    @Override
    @Transactional
    public Page<ProblemWithSample> findProblemAllByCategory(String category, String search, String difficulty,String solvedFilter, Pageable pageable) {
        String contact = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ProblemWithSample> problemWithSamples = new ArrayList<>();
        Page<Problem> problems = null;
        if(userRepo.existsByMobileNumber(contact) && solvedFilter != null && !solvedFilter.isEmpty()){
            problems = problemRepo.findByCategoryWithSolvedOrNotFilter(contact,category,search, difficulty, solvedFilter, pageable);
        }
        else{
            problems = problemRepo.findByCategoryWithFilter(category, search, difficulty, solvedFilter, pageable);
        }        for (Problem problem : problems.getContent()) {

            TestCase sampleTestcase = null;
            TestCase sampleOutput = null;

            for (TestCase testCase : problem.getTestcases()) {
                if (testCase.getFileName().equals("1.in")) {
                    sampleTestcase = testCase;
                } else if (testCase.getFileName().equals("1.out")) {
                    sampleOutput = testCase;
                }
            }

            List<String> sampleTestcaseContent = cloudinaryService.readCloudinaryFile(sampleTestcase.getFilePath());
            List<String> sampleOutputContent = cloudinaryService.readCloudinaryFile(sampleOutput.getFilePath());

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
        return new PageImpl<>(problemWithSamples, pageable, problems.getTotalElements());
    }
}
