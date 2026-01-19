package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.ProblemRequest;
import com.judge.myojudge.model.dto.ProblemResponse;
import com.judge.myojudge.model.dto.ProblemSampleTestCaseResponse;
import com.judge.myojudge.model.mapper.ProblemMapper;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.AuthService;
import com.judge.myojudge.service.ProblemService;
import com.judge.myojudge.service.TestCaseService;
import com.judge.myojudge.validation.ValidationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/problem")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;
    private final TestCaseService testCaseService;
    private final AuthService authService;
    private final ValidationService validationService;
    private final ProblemMapper problemMapper;

    @PostMapping(value="/v1/save" )
    @PreAuthorize("hasAnyRole('ADMIN','PROBLEM_EDITOR')")
    @Transactional
    public ResponseEntity<?> createProblemDetails(
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

    @GetMapping(value="/v1/get/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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

    @GetMapping(value="/v2/get/{id}")
    public ResponseEntity<ApiResponse<ProblemSampleTestCaseResponse>>getProblemForPage(@PathVariable Long id
    , HttpServletRequest request) throws IOException {
        String mobileOrEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ProblemSampleTestCaseResponse problemSampleTestCaseResponse = problemService.getProblemPerPageById(id,mobileOrEmail,request);
        ApiResponse<ProblemSampleTestCaseResponse> apiResponse=ApiResponse.<ProblemSampleTestCaseResponse>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Problem is fetched successfully.")
                .data(problemSampleTestCaseResponse)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }


    @GetMapping(value="/v1/category/{category}")
    public ResponseEntity<ApiResponse<Page<ProblemResponse>>>getProblemsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "") String difficulty,
            @RequestParam(name = "solved_filter",required = false, defaultValue = "") String solvedFilter,
            HttpServletRequest request
    )  {
        String mobileOrEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Pageable pageable= PageRequest.of(page,size);
        Page<ProblemResponse> problemResponses = problemService.findProblemsByCategory(request,mobileOrEmail,category,search,difficulty,solvedFilter,pageable);
        ApiResponse<Page<ProblemResponse>> apiResponse=ApiResponse.<Page<ProblemResponse>>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Problem is fetched by category("+category+")")
                .data(problemResponses)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PutMapping(value="/v1/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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

    @DeleteMapping(value="/v1/remove/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeAllProblem(
    ) throws IOException {
        problemService.deleteEachProblem();
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping(value="/v1/remove/{handle}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?>removeProblem(@PathVariable String handle
    ) throws IOException {
        problemService.deleteProblemByHandle( handle);
        return ResponseEntity.noContent().build() ;
    }


    @GetMapping(value="/v1/all")
    public ResponseEntity<ApiResponse<List<ProblemSampleTestCaseResponse>>>findAllProblem(
    )  {
        List<ProblemSampleTestCaseResponse> problemSampleTestCaseRespons = problemService.findProblemAll();
        ApiResponse<List<ProblemSampleTestCaseResponse>> problemWithSample=ApiResponse.<List<ProblemSampleTestCaseResponse>>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Fetching All Problems Successfully..!")
                .data(problemSampleTestCaseRespons)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(problemWithSample);
    }

}