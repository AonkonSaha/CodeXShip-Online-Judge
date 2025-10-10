package com.judge.myojudge.service.imp;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.judge.myojudge.exception.ProblemNotFoundException;
import com.judge.myojudge.model.dto.ProblemDTO;
import com.judge.myojudge.model.dto.ProblemWithSample;
import com.judge.myojudge.model.entity.Problem;
import com.judge.myojudge.model.entity.TestCase;
import com.judge.myojudge.repository.ProblemRepo;
import com.judge.myojudge.repository.TestCaseRepo;
import com.judge.myojudge.service.CloudinaryService;
import com.judge.myojudge.service.ProblemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
public class ProdProblemService implements ProblemService {

    private final ProblemRepo problemRepo;
    private final TestCaseRepo testCaseRepo;
    private final Cloudinary cloudinary;
    private final CloudinaryService cloudinaryService;

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
        Problem problem = new Problem();
        problem.setTitle(title);
        problem.setHandleName(handle);
        problem.setDifficulty(difficulty);
        problem.setType(type);
        problem.setCoins(coin);
        problem.setProblemStatement(problemStatement);
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
                deleteCloudinaryFile(testCase.getFileKey());
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
            deleteCloudinaryFile(testCase.getFileKey());
        }
        problemRepo.delete(problem.get());
    }

    private void deleteCloudinaryFile(String fileKey) {
        try {
            cloudinary.uploader().destroy(fileKey, ObjectUtils.asMap("resource_type", "raw"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file from Cloudinary with key: " + fileKey, e);
        }
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

    public ProblemDTO fetchOneProblemByID(long id) {
        Optional<Problem> problem = problemRepo.findById(id);
        if (problem.isEmpty()) {
            throw new ProblemNotFoundException("Problem not found with ID: " + id);
        }
        ProblemDTO problemDTO = new ProblemDTO();
        problemDTO.setTitle(problem.get().getTitle());
        problemDTO.setDifficulty(problem.get().getDifficulty());
        problemDTO.setType(problem.get().getType());
        problemDTO.setHandle(problem.get().getHandleName());
        problemDTO.setCoins(problem.get().getCoins());
        problemDTO.setProblemStatement(problem.get().getProblemStatement());
        return problemDTO;
    }

    @Override
    public void saveProblemWithId(long id, String title, String handle, String difficulty, String type,
                                  String problemStatement, List<MultipartFile> multipartFiles) throws IOException {
        Problem problem = problemRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found with ID: " + id));
        problem.setTitle(title);
        problem.setHandleName(handle);
        problem.setDifficulty(difficulty);
        problem.setType(type);
        problem.setProblemStatement(problemStatement);
        problemRepo.save(problem);

        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            Optional<Problem> tempProblem = problemRepo.findByHandleName(handle);
            if (tempProblem.isEmpty()) {
                throw new ProblemNotFoundException("Problem Not Found Handle By: " + handle);
            }

            for (MultipartFile testCaseFile : multipartFiles) {
                if (!testCaseFile.isEmpty()) {
                    Map uploadResult = cloudinaryService.uploadTestcase(testCaseFile);
                    TestCase testCase = new TestCase();
                    testCase.setFileName(testCaseFile.getOriginalFilename());
                    testCase.setFileKey(uploadResult.get("public_id").toString());
                    testCase.setFilePath(uploadResult.get("secure_url").toString());
                    testCase.setHandle(handle);
                    testCase.setProblem(tempProblem.get());
                    testCaseRepo.save(testCase);
                }
            }
        }
    }

    @Override
    @Transactional
    public Page<ProblemWithSample> findProblemAllByCategory(String category, String search, String difficulty, Pageable pageable) {
        System.out.println("I am in Category Function...........");
        List<ProblemWithSample> problemWithSamples = new ArrayList<>();
        List<Problem> p=problemRepo.findByType(category);
        Page<Problem> problems = problemRepo.findByCategoryORFilter(category, search, category, pageable);
        for (Problem problem : problems.getContent()) {

            TestCase sampleTestcase = null;
            TestCase sampleOutput = null;

            for (TestCase testCase : problem.getTestcases()) {
                System.out.println(testCase.getProblem().getProblemStatement());
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
            System.out.println("I am end category function...........");
        }
        return new PageImpl<>(problemWithSamples, pageable, problems.getTotalElements());
    }
}
