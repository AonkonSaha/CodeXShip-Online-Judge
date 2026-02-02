package com.judge.myojudge.service;

import com.judge.myojudge.model.dto.ProblemResponse;
import com.judge.myojudge.model.dto.ProblemSampleTcResponse;
import com.judge.myojudge.model.entity.Problem;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProblemService {

     List<ProblemSampleTcResponse> findProblemAll();

     void saveProblem(
            String title, String handle, String difficulty,String type,
            Long coin,double timeLimit,double memoryLimit, String problemStatement, String explanation
    );

     boolean findProblemByHandleExit(String handle) ;

     Optional<Problem> findProblemByHandle(String handle);

     void deleteEachProblem() throws IOException;

     void deleteProblemByHandle(String handle) throws IOException;

     ProblemSampleTcResponse getProblemPerPageById(Long id, String mobileOrEmail, HttpServletRequest request);

     Problem getProblemByID(long id);

     void saveProblemWithId(
            long id, String title, String handle, String difficulty, String type,
            String problemStatement,String explanation , Long coins,
            double timeLimit,double memoryLimit, List<MultipartFile> multipartFiles
    ) throws IOException;

     Page<ProblemResponse> findProblemsByCategory(
            HttpServletRequest request,String mobileOrEmail,String category,String search, String difficulty,
            String solvedFilter, Pageable pageable
    );

    List<Object[]> getProblemSolveStatus(Long id, String email);
}
