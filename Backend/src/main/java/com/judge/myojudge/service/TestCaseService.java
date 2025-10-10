package com.judge.myojudge.service;

import com.judge.myojudge.model.dto.ExecuteTestCase;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TestCaseService {

    public void saveTestCases(String handle, String name, List<MultipartFile> testCaseFiles) throws IOException;
    List<ExecuteTestCase> getTestCaseWithFile(Long problemId);
}
