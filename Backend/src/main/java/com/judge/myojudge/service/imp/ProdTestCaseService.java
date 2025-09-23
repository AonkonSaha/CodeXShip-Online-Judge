package com.judge.myojudge.service.imp;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.judge.myojudge.exception.ProblemNotFoundException;
import com.judge.myojudge.model.entity.Problem;
import com.judge.myojudge.model.entity.TestCase;
import com.judge.myojudge.repository.TestCaseRepo;
import com.judge.myojudge.service.ProblemService;
import com.judge.myojudge.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Profile("prod")
public class ProdTestCaseService implements TestCaseService {

    @Value("${testcase.folder.path}")
    public String testCaseFolderPath;
    private final TestCaseRepo testCaseRepo;
    private final ProblemService problemService;
    @Autowired
    AmazonS3 s3Client;
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Override
    public void saveTestCases(String handle, String name, List<MultipartFile> testCaseFiles) throws IOException {

        Optional<Problem> problem = problemService.findProblemByHandle(handle);
        if(problem.isEmpty()){
            throw new ProblemNotFoundException("Problem Not Found Handle By: "+handle);
        }
        TestCase testCase;
        for(MultipartFile file:testCaseFiles)
        {
            String s3Key = uploadFile(file);
            String fileUrl = getFileUrl(s3Key);
            testCase = TestCase.builder()
                    .fileName(file.getOriginalFilename())
                    .filePath(fileUrl)
                    .handle(handle)
                    .fileKey(s3Key)
                    .build();
            testCase.setProblem(problem.get());
            testCaseRepo.save(testCase);
        }
    }
    public String uploadFile(MultipartFile file) throws IOException {
        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());

        s3Client.putObject(new PutObjectRequest(bucketName, uniqueFileName, file.getInputStream(), metadata));

        return uniqueFileName;
    }

    public String getFileUrl(String fileKey) {
        return s3Client.getUrl(bucketName, fileKey).toString();
    }


}
