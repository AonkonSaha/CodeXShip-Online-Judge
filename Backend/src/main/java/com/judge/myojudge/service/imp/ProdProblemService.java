package com.judge.myojudge.service.imp;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.judge.myojudge.exception.ProblemNotFoundException;
import com.judge.myojudge.model.dto.ProblemDTO;
import com.judge.myojudge.model.dto.ProblemDetailWithSample;
import com.judge.myojudge.model.dto.ProblemWithSample;
import com.judge.myojudge.model.dto.TestcaseDTO;
import com.judge.myojudge.model.entity.Problem;
import com.judge.myojudge.model.entity.TestCase;
import com.judge.myojudge.repository.ProblemRepo;
import com.judge.myojudge.repository.TestCaseRepo;
import com.judge.myojudge.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Profile("prod")
public class ProdProblemService implements ProblemService {
    @Value("${testcase.folder.path}")
    public String testCaseFolderPath;
    private final ProblemRepo problemRepo;
    private final TestCaseRepo testCaseRepo;
    private final AmazonS3 s3Client;
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

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

            // Find the test case files for this problem
            for (TestCase testCase : problem.getTestcases()) {
                if (testCase.getFileName().equals("1.in")) {
                    sampleTestcase = testCase;
                } else if (testCase.getFileName().equals("1.out")) {
                    sampleOutput = testCase;
                }
            }
            // Read test case file content from S3
            List<String> sampleTestcaseContent = readS3File(sampleTestcase);
            List<String> sampleOutputContent = readS3File(sampleOutput);

            problemWithSample.setSampleTestcase(sampleTestcaseContent);
            problemWithSample.setSampleOutput(sampleOutputContent);

            problemList.add(problemWithSample);
        }
        return problemList;
    }

    private List<String> readS3File(TestCase testCase) {
        List<String> lines = new ArrayList<>();
        if (testCase != null) {
            try {
                S3Object s3Object = downloadFile(testCase.getFileKey());
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error reading file from S3: " + testCase.getFileName(), e);
            }
        }
        return lines;
    }
    public S3Object downloadFile(String fileKey) {
        if (fileKey == null || fileKey.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        return s3Client.getObject(bucketName, fileKey);

    }

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
    public void deleteEachProblem() {
        List<Problem>problems=problemRepo.findAll();
        if (problems.isEmpty()){
            throw new ProblemNotFoundException("There is no problem..!");
        }
        for(Problem problem:problems)
        {
            for(TestCase testCase:problem.getTestcases())
            {
                s3Client.deleteObject(new DeleteObjectRequest(bucketName, testCase.getFileKey()));
            }

        }

        problemRepo.deleteAll();
    }

    @Override
    public void deleteProblemByHandle(String handle){
        Optional<Problem> problem=problemRepo.findByHandleName(handle);
        if(problem.isEmpty()){
            throw new ProblemNotFoundException("Problem Not Found Handle By: "+handle);
        }

        for(TestCase testCase:problem.get().getTestcases())
        {
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, testCase.getFileKey()));
//            Path path = Paths.get(testCase.getFilePath()); // Convert the file path string to a Path object
//            if (Files.exists(path)) {
//                Files.delete(path); // Delete the file
//            }
        }
        problemRepo.delete(problem.get());
    }


    @Override
    public ProblemWithSample findProblemByID(Long id) {
        Problem problem = problemRepo.findById(id).orElseThrow(() -> new ProblemNotFoundException("Problem not found"));

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

        // Find the test case files for this problem
        for (TestCase testCase : problem.getTestcases()) {
            if (testCase.getFileName().equals("1.in")) {
                sampleTestcase = testCase;
            } else if (testCase.getFileName().equals("1.out")) {
                sampleOutput = testCase;
            }
        }

        // Read test case file content from S3
        List<String> sampleTestcaseContent = readS3File(sampleTestcase);
        List<String> sampleOutputContent = readS3File(sampleOutput);


        problemWithSample.setSampleTestcase(sampleTestcaseContent);
        problemWithSample.setSampleOutput(sampleOutputContent);

        // print the input/output here for debugging purposes
        problemWithSample.getSampleTestcase().forEach(System.out::println);
        problemWithSample.getSampleOutput().forEach(System.out::println);

        return problemWithSample;
    }


    public ProblemDTO fetchOneProblemByID(long id) {
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
//        List<String> filePaths=new ArrayList<>();
//        for(TestCase testCase:problem.get().getTestcases()){
//            filePaths.add(testCase.getFilePath());
//        }
//        problemDTO.setTestcasesFilePaths(filePaths);
        return problemDTO;
    }


    @Override
    public void saveProblemWithId(long id, String title, String handle, String difficulty,String type,
                                  String problemStatement, List<MultipartFile> multipartFiles) throws IOException {

        // Retrieve the existing problem by ID or throw an exception
        Problem problem = problemRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found with ID: " + id));

        // Update problem details
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
                throw new ProblemNotFoundException("Problem Not Found Handle By: "+handle);
            }
            for (MultipartFile testCaseFile : multipartFiles) {
                if (!testCaseFile.isEmpty()) {
                    // Generate a unique file name using UUID
                    String fileName = UUID.randomUUID().toString() + "_" + testCaseFile.getOriginalFilename();

                    // Upload file to S3 and get the file's S3 key
                    String fileKey = uploadFile(testCaseFile);

                    // Save test case details
                    TestCase testCase = new TestCase();
                    testCase.setFileName(testCaseFile.getOriginalFilename());
                    testCase.setFileKey(fileKey);  // Save S3 key in the database
                    testCase.setHandle(handle);
                    testCase.setProblem(tempProblem.get());
                    testCaseRepo.save(testCase);
                }
            }
        }
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());

        s3Client.putObject(new PutObjectRequest(bucketName, uniqueFileName, file.getInputStream(), metadata));

        return uniqueFileName;
    }

    @Override
    public List<ProblemWithSample> findProblemAllByCategory(String category) {
        List<ProblemWithSample>problemWithSamples=new ArrayList<>();
        List<Problem> problems=problemRepo.findByType(category);
        for(Problem problem:problems)
        {
            TestCase sampleTestcase = null;
            TestCase sampleOutput = null;

            // Find the test case files for this problem
            for (TestCase testCase : problem.getTestcases()) {
                if (testCase.getFileName().equals("1.in")) {
                    sampleTestcase = testCase;
                } else if (testCase.getFileName().equals("1.out")) {
                    sampleOutput = testCase;
                }
            }

            // Read test case file content from S3
            List<String> sampleTestcaseContent = readS3File(sampleTestcase);
            List<String> sampleOutputContent = readS3File(sampleOutput);

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
        return problemWithSamples;
    }
}
