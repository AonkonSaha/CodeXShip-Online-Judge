package com.judge.myojudge.service;

import com.judge.myojudge.model.dto.ProblemDTO;
import com.judge.myojudge.model.dto.ProblemWithSample;
import com.judge.myojudge.model.entity.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProblemService {



    public List<ProblemWithSample> findProblemAll();

    public void saveProblem(String title, String handle, String difficulty,
                            String type,Long coin, String problemStatement, String explanation);

    public boolean findProblemByHandleExit(String handle) ;

    public Optional<Problem> findProblemByHandle(String handle);

    public void deleteEachProblem() throws IOException;

    public void deleteProblemByHandle(String handle) throws IOException;

    public ProblemWithSample findProblemByID(Long id);

    public ProblemDTO fetchOneProblemByID(long id);

    public void saveProblemWithId(long id, String title, String handle, String difficulty, String type,
                                  String problemStatement,String explanation , Long coins, List<MultipartFile> multipartFiles) throws IOException;

    public Page<ProblemWithSample> findProblemAllByCategory(String category,String search, String difficulty, String solvedFilter, Pageable pageable);

}
