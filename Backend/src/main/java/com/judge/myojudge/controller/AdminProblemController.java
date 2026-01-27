package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.ProblemSampleTcResponse;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/problems")
@RequiredArgsConstructor
public class AdminProblemController {
    private final ProblemService problemService;

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeAllProblem(
    ) throws IOException {
        problemService.deleteEachProblem();
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{handle}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void>removeProblem(@PathVariable String handle
    ) throws IOException {
        problemService.deleteProblemByHandle( handle);
        return ResponseEntity.noContent().build() ;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ProblemSampleTcResponse>>>findAllProblem(
    )  {
        List<ProblemSampleTcResponse> problemSampleTcResponse = problemService.findProblemAll();
        ApiResponse<List<ProblemSampleTcResponse>> problemWithSample=ApiResponse.<List<ProblemSampleTcResponse>>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Fetching All Problems Successfully..!")
                .data(problemSampleTcResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(problemWithSample);
    }

}
