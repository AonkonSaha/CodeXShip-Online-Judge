package com.judge.myojudge.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.judge.myojudge.model.dto.ProblemDTO;
import com.judge.myojudge.model.dto.ProblemDetailWithSample;
import com.judge.myojudge.model.dto.TestcaseDTO;
import com.judge.myojudge.model.entity.Problem;
import com.judge.myojudge.model.entity.TestCase;
import com.judge.myojudge.repository.ProblemRepo;
import com.judge.myojudge.repository.TestCaseRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProblemService {

    @Value("${testcase.folder.path}")
    public String testCaseFolderPath;

    @Autowired
    ProblemRepo problemRepo;
    @Autowired
    TestCaseRepo testCaseRepo;
    @Autowired
    AmazonS3 s3Client;
    @Value("${aws.s3.bucket-name}")
    private String bucketName;



    public List<TestcaseDTO> findProblemAll() {
        List<TestcaseDTO> problemList = new ArrayList<>();
        List<Problem> problems = problemRepo.findAll();

        for (Problem problem : problems) {
            TestcaseDTO testcaseDTO = new TestcaseDTO();
            testcaseDTO.setId(problem.getId());
            testcaseDTO.setTitle(problem.getTitle());
            testcaseDTO.setHandle(problem.getHandleName());
            testcaseDTO.setProblemStatement(problem.getProblemStatement());
            testcaseDTO.setType(problem.getType());
            testcaseDTO.setDifficulty(problem.getDifficulty());

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

            testcaseDTO.setSampleTestcase(sampleTestcaseContent);
            testcaseDTO.setSampleOutput(sampleOutputContent);

            problemList.add(testcaseDTO);
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

    public void saveProblem(String title,
                            String handle,
                            String difficulty,
                            String type,
                            String problemStatement
                            )  {
        Problem problem=new Problem();
        problem.setTitle(title);
        problem.setHandleName(handle);
        problem.setDifficulty(difficulty);
        problem.setType(type);
        problem.setProblemStatement(problemStatement);
        problemRepo.save(problem);
    }

    public boolean findProblemByHandleExit(String handle) {
        return problemRepo.existsByHandleName(handle);
    }
    public Problem findProblemByHandle(String handle) {
        return problemRepo.findByHandleName(handle);
    }

    public String deleteEachProblem() throws IOException {
        if(problemRepo.count()==0)return "Problem doesn't exit";
        List<Problem>problems=problemRepo.findAll();
        for(Problem problem:problems)
        {
            for(TestCase testCase:problem.getTestcases())
            {

                s3Client.deleteObject(new DeleteObjectRequest(bucketName, testCase.getFileKey()));
//                Path path = Paths.get(testCase.getFilePath()); // Convert the file path string to a Path object
//
//                if (Files.exists(path)) {
//                    Files.delete(path); // Delete the file
//                }
            }

        }

        problemRepo.deleteAll();
        return "ProblemsDeleted";
    }

    public String deleteProblemByHandle(String handle) throws IOException {
        if(!problemRepo.existsByHandleName(handle))return "Problem doesn't exit";
        Problem problem=problemRepo.findByHandleName(handle);
        for(TestCase testCase:problem.getTestcases())
        {
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, testCase.getFileKey()));
//            Path path = Paths.get(testCase.getFilePath()); // Convert the file path string to a Path object
//            if (Files.exists(path)) {
//                Files.delete(path); // Delete the file
//            }
        }
        problemRepo.delete(problem);
        return "ProblemDelete";
    }


    public ProblemDetailWithSample findProblemByID(Long id) {
    // Fetch the problem by ID, throw an exception if not found
    Problem problem = problemRepo.findById(id).orElseThrow(() -> new RuntimeException("Problem not found"));

    // Initialize the ProblemDetailWithSample object
    ProblemDetailWithSample problemDetail = new ProblemDetailWithSample();
    problemDetail.setId(problem.getId());
    problemDetail.setName(problem.getTitle());
    problemDetail.setStatement(problem.getProblemStatement());
    problemDetail.setDifficulty(problem.getDifficulty());
    problemDetail.setSolve(false);

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


        problemDetail.setInput(sampleTestcaseContent);
        problemDetail.setOutput(sampleOutputContent);

    // Optionally, you can print the input/output here for debugging purposes
    problemDetail.getInput().forEach(System.out::println);
    problemDetail.getOutput().forEach(System.out::println);

    return problemDetail;
}


    public ProblemDTO fetchOneProblemByID(long idd) {
        Problem problem= problemRepo.findById(idd).orElseThrow();
        ProblemDTO problemDTO=new ProblemDTO();
        problemDTO.setTitle(problem.getTitle());
        problemDTO.setDifficulty(problem.getDifficulty());
        problemDTO.setType(problem.getType());
        problemDTO.setHandle(problem.getHandleName());
        problemDTO.setProblemStatement(problem.getProblemStatement());
        problemDTO.setTestcases(problem.getTestcases());
        return problemDTO;
    }


    public void saveProblemWithId(long id, String title, String handle, String difficulty, String type,
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
            Problem tempProblem = problemRepo.findByHandleName(handle);  // Find the problem by handle
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
                    testCase.setProblem(tempProblem);
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

    public List<TestcaseDTO> findProblemAllByCategory(String category) {
        List<TestcaseDTO>problemsWithTestCases=new ArrayList<>();
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

            TestcaseDTO testcaseDTO = TestcaseDTO.builder()
                    .id(problem.getId())
                    .problemStatement(problem.getProblemStatement())
                    .title(problem.getTitle())
                    .handle(problem.getHandleName())
                    .type(problem.getType())
                    .difficulty(problem.getDifficulty())
                    .sampleTestcase(sampleTestcaseContent)
                    .sampleOutput(sampleOutputContent)
                    .build();
            problemsWithTestCases.add(testcaseDTO);

        }
        return problemsWithTestCases;
    }


//public void saveProblemWithId(long id, String title, String handle, String difficulty, String type,
//                              String problemStatement, List<MultipartFile> multipartFiles) throws IOException {
//
//    Problem problem = problemRepo.findById(id)
//            .orElseThrow(() -> new IllegalArgumentException("Problem not found with ID: " + id));
//
//    problem.setTitle(title);
//    problem.setHandle(handle);
//    problem.setDifficulty(difficulty);
//    problem.setType(type);
//    problem.setProblemStatement(problemStatement);
//    problemRepo.save(problem);
//
//    // Save Test Case Files if provided
//    if (multipartFiles != null && !multipartFiles.isEmpty()) {
//        Problem tempProblem = problemRepo.findByHandle(handle);
//        for (MultipartFile testCaseFile : multipartFiles) {
//            if (!testCaseFile.isEmpty()) {
//                String fileName = handle + "_" + testCaseFile.getOriginalFilename();
//                Path filePath = Paths.get(testCaseFolderPath, fileName);
//
//                // Save file
//                Files.write(filePath, testCaseFile.getBytes());
//
//                // Save test case details
//                TestCase testCase = new TestCase();
//                testCase.setFileName(testCaseFile.getOriginalFilename());
//                testCase.setFilePath(filePath.toString());
//                testCase.setHandle(handle);
//                testCase.setProblem(tempProblem);
//                testCaseRepo.save(testCase);
//            }
//        }
//    }
//}


    // Helper method to read lines from a file and return as a List of Strings
//    private List<String> readFileLines(String filePath) {
//        List<String> lines = new ArrayList<>();
//        Path path = Paths.get(filePath);
//
//        try (BufferedReader reader = Files.newBufferedReader(path)) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                lines.add(line);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Error reading file: " + filePath, e);
//        }
//
//        return lines;
//    }


//    public String deleteEachProblem() throws IOException {
//        if(problemRepo.count()==0)return "Problem doesn't exit";
//        List<Problem>problems=problemRepo.findAll();
//        for(Problem problem:problems)
//        {
//            for(TestCase testCase:problem.getTestcases())
//            {
//
//                Path path = Paths.get(testCase.getFilePath()); // Convert the file path string to a Path object
//
//                if (Files.exists(path)) {
//                    Files.delete(path); // Delete the file
//                }
//            }
//
//        }
//
//        problemRepo.deleteAll();
//        return "ProblemsDeleted";
//    }


//    public ProblemDetailWithSample findProblemByID(Long id) {
//        // Fetch the problem by ID, throw an exception if not found
//        Problem problem = problemRepo.findById(id).orElseThrow(() -> new RuntimeException("Problem not found"));
//
//        // Initialize the ProblemDetailWithSample object
//        ProblemDetailWithSample problemDetail = new ProblemDetailWithSample();
//        problemDetail.setId(problem.getId());
//        problemDetail.setName(problem.getTitle());
//        problemDetail.setStatement(problem.getProblemStatement());
//        problemDetail.setDifficulty(problem.getDifficulty());
//        problemDetail.setSolve(false);
//
//        // Find the input and output test cases
//        TestCase sampleTestcase = null;
//        TestCase sampleOutput = null;
//
//        // Loop through test cases to find the relevant ones
//        for (TestCase testCase : problem.getTestcases()) {
//            if ("1.in".equals(testCase.getFileName())) {
//                sampleTestcase = testCase;
//            } else if ("1.out".equals(testCase.getFileName())) {
//                sampleOutput = testCase;
//            }
//        }
//
//        // Ensure both sampleTestcase and sampleOutput are found before proceeding
//        if (sampleTestcase != null && sampleOutput != null) {
//            problemDetail.setInput(readFileLines(sampleTestcase.getFilePath()));
//            problemDetail.setOutput(readFileLines(sampleOutput.getFilePath()));
//        } else {
//            throw new RuntimeException("Test cases not found for problem ID: " + id);
//        }
//
//        // Optionally, you can print the input/output here for debugging purposes
//        problemDetail.getInput().forEach(System.out::println);
//        problemDetail.getOutput().forEach(System.out::println);
//
//        return problemDetail;
//    }


//    public void saveProblemWithId(long id, String title, String handle, String difficulty, String type, String problemStatement, List<MultipartFile> multipartFiles) throws IOException {
//        System.out.println("I");
//        Problem problem=problemRepo.findById(id).orElseThrow();
//        problem.setTitle(title);
//        problem.setHandle(handle);
//        problem.setDifficulty(difficulty);
//        problem.setType(type);
//        problem.setProblemStatement(problemStatement);
//        System.out.println("am");
//        problemRepo.save(problem);
//        System.out.println("saha");
//        for(MultipartFile testCaseFile:multipartFiles)
//        {
//            System.out.println("bolo");
//            TestCase testCase=testCaseRepo.findByHandle(handle).orElseThrow();
//            System.out.println("ram");
//            String fileName = handle + "_" + testCaseFile.getOriginalFilename();
//            Path filePath = Paths.get(testCaseFolderPath, fileName);
//            Files.write(filePath, testCaseFile.getBytes());
//            testCase.setFileName(testCaseFile.getOriginalFilename());
//            testCase.setFilePath(filePath.toString());
//            testCase.setHandle(handle);
//            System.out.println("chandra");
//            Problem tempProblem=problemRepo.findByHandle(handle);
//            System.out.println("saha");
//            testCase.setProblem(tempProblem);
//            testCaseRepo.save(testCase);
//            System.out.println("ki");
//        }
//
//    }



//    public List<ProblemWithTestCases> findProblemAll() {
//        List<ProblemWithTestCases>problemList=new ArrayList<>();
//        List<Problem>problems= problemRepo.findAll();
//        for(Problem problem:problems)
//        {
//            ProblemWithTestCases problemWithTestCases=new ProblemWithTestCases();
//            problemWithTestCases.setId(problem.getId());
//            problemWithTestCases.setTitle(problem.getTitle());
//            problemWithTestCases.setHandle(problem.getHandle());
//            problemWithTestCases.setProblemStatement(problem.getProblemStatement());
//            problemWithTestCases.setType(problem.getType());
//            problemWithTestCases.setDifficulty(problem.getDifficulty());
//
//            TestCase sampleTestcase=new TestCase();
//            TestCase sampleOutput=new TestCase();
//            for(TestCase testCase:problem.getTestcases())
//            {
//
//                if(testCase.getFileName().equals("1.in"))
//                {
//                    sampleTestcase=testCase;
////                    System.out.println("Test2: "+testCase.getFileName());
//                }
//                else if(testCase.getFileName().equals("1.out"))
//                {
//                    sampleOutput=testCase;
////                    System.out.println("Test2: "+testCase.getFileName());
//                }
//            }
//
//
//           Path file1=Paths.get(sampleTestcase.getFilePath());
//            Path file2=Paths.get(sampleOutput.getFilePath());
//
////
//            // Create a list to hold the lines
//            List<String> lines = new ArrayList<>();
//
//            try (BufferedReader reader = Files.newBufferedReader(file1)) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    lines.add(line);
////                    System.out.println(line);
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            problemWithTestCases.setSampleTestcase(lines);
//            lines.clear();
//            // Read the file line by line
//            try (BufferedReader reader = Files.newBufferedReader(file2)) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    lines.add(line);
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
////            for(String s:lines) System.out.println(s);
//            problemWithTestCases.setSampleOutput(lines);
//            problemList.add(problemWithTestCases);
//        }
//        return problemList;
//    }


}