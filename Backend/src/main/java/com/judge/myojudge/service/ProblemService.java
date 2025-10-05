package com.judge.myojudge.service;

import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.judge.myojudge.model.dto.ProblemDTO;
import com.judge.myojudge.model.dto.ProblemDetailWithSample;
import com.judge.myojudge.model.dto.ProblemWithSample;
import com.judge.myojudge.model.dto.TestcaseDTO;
import com.judge.myojudge.model.entity.Problem;
import com.judge.myojudge.model.entity.TestCase;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProblemService {



    public List<ProblemWithSample> findProblemAll();

    public void saveProblem(String title, String handle, String difficulty,
                            String type, String problemStatement);

    public boolean findProblemByHandleExit(String handle) ;

    public Optional<Problem> findProblemByHandle(String handle);

    public void deleteEachProblem() throws IOException;

    public void deleteProblemByHandle(String handle) throws IOException;

    public ProblemWithSample findProblemByID(Long id);

    public ProblemDTO fetchOneProblemByID(long id);

    public void saveProblemWithId(long id, String title, String handle, String difficulty, String type,
                                  String problemStatement, List<MultipartFile> multipartFiles) throws IOException;

    public List<ProblemWithSample> findProblemAllByCategory(String category);

}
