package com.judge.myojudge.service.imp;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.judge.myojudge.model.entity.Problem;
import com.judge.myojudge.model.entity.TestCase;
import com.judge.myojudge.repository.TestCaseRepo;
import com.judge.myojudge.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TestCaseServiceImp implements TestCaseService {

    @Value("${testcase.folder.path}")
    public String testCaseFolderPath;
    private final TestCaseRepo testCaseRepo;
    private final ProblemServiceImp problemServiceImp;
    @Autowired
    AmazonS3 s3Client;
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Override
    public void saveTestCases(String handle, String name, List<MultipartFile> testCaseFiles) throws IOException {
        for(MultipartFile file:testCaseFiles)
        {
            String s3Key = uploadFile(file);
            String fileUrl = getFileUrl(s3Key);
            TestCase testCase = TestCase.builder()
                    .fileName(file.getOriginalFilename())
                    .filePath(fileUrl)
                    .handle(handle)
                    .fileKey(s3Key)
                    .build();
            Problem problem= problemServiceImp.findProblemByHandle(handle);
            testCase.setProblem(problem);
            testCaseRepo.save(testCase);
        }
    }
    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());

        s3Client.putObject(new PutObjectRequest(bucketName, uniqueFileName, file.getInputStream(), metadata));

        return uniqueFileName;
    }

    @Override
    public String getFileUrl(String fileKey) {
        return s3Client.getUrl(bucketName, fileKey).toString();
    }












//    public void saveTestCases(String handle, String name, List<MultipartFile> testCaseFiles) throws IOException {
//        for(MultipartFile testCaseFile:testCaseFiles)
//        {
//            // Save file to project folder
////            String fileName = UUID.randomUUID() + "_" + testCaseFile.getOriginalFilename();
//            String fileName = handle + "_" + testCaseFile.getOriginalFilename();
////        System.out.println("Test: "+UUID.randomUUID());
//            Path filePath = Paths.get(testCaseFolderPath, fileName);
//            // Files.createDirectories(filePath.getParent());//Automatically directory create for file save
//            Files.write(filePath, testCaseFile.getBytes());
////        long fileSize=Files.size(filePath);
////        System.out.println("Size: "+fileSize);
//
//            TestCase testCase=new TestCase();
//            testCase.setFileName(testCaseFile.getOriginalFilename());
//            testCase.setFilePath(filePath.toString());
//            testCase.setHandle(handle);
//            Problem problem=problemService.findProblemByHandle(handle);
//            testCase.setProblem(problem);
//            testCaseRepo.save(testCase);
//
//        }
//
//
////        return problem;
//    }


}
