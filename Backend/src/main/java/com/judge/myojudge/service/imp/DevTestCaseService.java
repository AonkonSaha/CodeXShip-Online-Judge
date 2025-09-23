package com.judge.myojudge.service.imp;

import com.judge.myojudge.exception.ProblemNotFoundException;
import com.judge.myojudge.model.entity.Problem;
import com.judge.myojudge.model.entity.TestCase;
import com.judge.myojudge.repository.TestCaseRepo;
import com.judge.myojudge.service.ProblemService;
import com.judge.myojudge.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Profile("dev")
public class DevTestCaseService implements TestCaseService {

    private final ProblemService problemService;
    private final TestCaseRepo testCaseRepo;

    @Value("${testcase.folder.path}")
    public String testCaseFolderPath;

        @Override
        public void saveTestCases(String handle, String name, List<MultipartFile> testCaseFiles) throws IOException {
         for(MultipartFile testCaseFile:testCaseFiles)
      {
//            // Save file to project folder
////            String fileName = UUID.randomUUID() + "_" + testCaseFile.getOriginalFilename();
            String fileName = handle + "_" + testCaseFile.getOriginalFilename();
////        System.out.println("Test: "+UUID.randomUUID());
            Path filePath = Paths.get(testCaseFolderPath, fileName);
//            // Files.createDirectories(filePath.getParent());//Automatically directory create for file save
            Files.write(filePath, testCaseFile.getBytes());
////        long fileSize=Files.size(filePath);
////        System.out.println("Size: "+fileSize);

            TestCase testCase=new TestCase();
            testCase.setFileName(testCaseFile.getOriginalFilename());
            testCase.setFilePath(filePath.toString());
            testCase.setHandle(handle);
            Optional<Problem> problem=problemService.findProblemByHandle(handle);
            if(problem.isEmpty()){
                throw new ProblemNotFoundException("Problem Not Found Handle By: "+handle);
            }
            testCase.setProblem(problem.get());
            testCaseRepo.save(testCase);

        }


//        return problem;
    }

    }
