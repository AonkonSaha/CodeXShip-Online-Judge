package com.judge.myojudge.service;

import com.judge.myojudge.model.dto.ProblemDTO;
import com.judge.myojudge.model.dto.ProblemWithSample;
import com.judge.myojudge.model.entity.Problem;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProblemService {

    public List<ProblemWithSample> findProblemAll();

    public void saveProblem(
            String title, String handle, String difficulty,String type,
            Long coin,double timeLimit,double memoryLimit, String problemStatement, String explanation
    );

    public boolean findProblemByHandleExit(String handle) ;

    public Optional<Problem> findProblemByHandle(String handle);

    public void deleteEachProblem() throws IOException;

    public void deleteProblemByHandle(String handle) throws IOException;

    public ProblemWithSample getProblemPerPageById(Long id, HttpServletRequest request);

    public ProblemDTO updateProblemByID(long id);

    public void saveProblemWithId(
            long id, String title, String handle, String difficulty, String type,
            String problemStatement,String explanation , Long coins,
            double timeLimit,double memoryLimit, List<MultipartFile> multipartFiles
    ) throws IOException;

    public Page<ProblemWithSample> findProblemAllByCategory(
            String mobileOrEmail,String category,String search, String difficulty,
            String solvedFilter, Pageable pageable
    );

}
