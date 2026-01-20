package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.ProblemRequest;
import com.judge.myojudge.model.dto.ProblemResponse;
import com.judge.myojudge.model.mapper.ProblemMapper;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.ProblemService;
import com.judge.myojudge.service.TestCaseService;
import com.judge.myojudge.validation.ValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/author/problems")
@RequiredArgsConstructor
public class AuthorProblemController {
    private final ProblemService problemService;
    private final TestCaseService testCaseService;
    private final ValidationService validationService;
    private final ProblemMapper problemMapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROBLEM_EDITOR')")
    @Transactional
    public ResponseEntity<ApiResponse<String>> createProblem(
            @RequestParam("title") String title,
            @RequestParam("handle")String handle,
            @RequestParam("difficulty")String difficulty,
            @RequestParam("type")String type,
            @RequestParam("coin")Long coin,
            @RequestParam("time_limit")double timeLimit,
            @RequestParam("memory_limit")double memoryLimit,
            @RequestParam("problemStatement") String problemStatement,
            @RequestParam("explanation") String explanation,
            @RequestParam("testCaseFile") List<MultipartFile> multipartFiles

    ) throws IOException {
        validationService.validateProblemDetails(new ProblemRequest(title, handle, difficulty, type,
                problemStatement,explanation, multipartFiles));
        problemService.saveProblem(title,handle,difficulty,type,coin,timeLimit,memoryLimit,problemStatement,explanation);
        testCaseService.saveTestCases(handle,title, multipartFiles);
        ApiResponse<String> apiResponse=ApiResponse.<String>builder()
                .success(true)
                .statusCode(HttpStatus.CREATED.value())
                .message("Problem Created Successfully..!")
                .data("Problem Created Successfully..!")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);

    }

    @GetMapping(value="/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROBLEM_EDITOR')")
    @Transactional
    public ResponseEntity<ApiResponse<ProblemResponse>>getProblemForEdit(@PathVariable Long id
    ) throws IOException {
        ProblemResponse problemResponse = problemMapper.toProblemResponse(
                problemService.getProblemByID(id)
        );
        ApiResponse<ProblemResponse> apiResponse= ApiResponse.<ProblemResponse>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Problem is fetched for edit.")
                .data(problemResponse)
                .build();

        return  ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROBLEM_EDITOR')")
    public ResponseEntity<Void> updateProblemDetails(
            @PathVariable("id") Long problemId,
            @RequestParam("title") String title,
            @RequestParam("handle") String handle,
            @RequestParam("difficulty") String difficulty,
            @RequestParam("type") String type,
            @RequestParam("coin") Long coins,
            @RequestParam("time_limit")double timeLimit,
            @RequestParam("memory_limit")double memoryLimit,
            @RequestParam("problemStatement") String problemStatement,
            @RequestParam("explanation") String explanation,
            @RequestParam(value = "testCaseFile", required = false) List<MultipartFile> multipartFiles
    ) throws IOException {
//            validationService.validateProblemDetails(new ProblemDTO(title, handle, difficulty,
//                                                     type,problemStatement, multipartFiles));

        problemService.saveProblemWithId(problemId, title, handle, difficulty, type, problemStatement,explanation ,coins,timeLimit,memoryLimit, multipartFiles);
        return ResponseEntity.noContent().build();

    }
}
