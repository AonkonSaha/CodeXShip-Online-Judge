package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.ProblemDTO;
import com.judge.myojudge.model.dto.ProblemWithSample;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.AuthService;
import com.judge.myojudge.service.ProblemService;
import com.judge.myojudge.service.TestCaseService;
import com.judge.myojudge.validation.ValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping(value="/v1/get/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProblemDTO>>fetchProblemForUpdate(@PathVariable Long id
    ) throws IOException {
        ProblemDTO problemDTO= problemService.fetchOneProblemByID(id);
        ApiResponse<ProblemDTO> apiResponse= ApiResponse.<ProblemDTO>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Fetching One Problem Success..!")
                .data(problemDTO)
                .build();

        return  ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping(value="/v2/get/{id}")
    public ResponseEntity<ApiResponse<ProblemWithSample>>fetchProblemForPage(@PathVariable Long id
    ) {
        ProblemWithSample problemWithSample= problemService.findProblemByID(id);
        ApiResponse<ProblemWithSample> apiResponse=ApiResponse.<ProblemWithSample>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Problem Searched Successfully")
                .data(problemWithSample)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping(value="/v1/all")
    public ResponseEntity<ApiResponse<List<ProblemWithSample>>>findAllProblem(
    )  {
        List<ProblemWithSample> problemWithSamples = problemService.findProblemAll();
        ApiResponse<List<ProblemWithSample>> problemWithSample=ApiResponse.<List<ProblemWithSample>>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Fetching All Problems Successfully..!")
                .data(problemWithSamples)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(problemWithSample);
    }
    @GetMapping(value="/v1/category/{category}")
    public ResponseEntity<ApiResponse<Page<ProblemWithSample>>>searchAllProblemWithCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String difficulty
    )  {
        Pageable pageable= PageRequest.of(page,size);
        Page<ProblemWithSample> problemWithSamples = problemService.findProblemAllByCategory(category,search,difficulty,pageable);
        ApiResponse<Page<ProblemWithSample>> problemWithSample=ApiResponse.<Page<ProblemWithSample>>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Fetching Problems With Category Successfully..!")
                .data(problemWithSamples)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(problemWithSample);
    }


    @PostMapping(value="/v1/save" )
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> createProblemDetails(
            @RequestParam("title") String title,
            @RequestParam("handle")String handle,
            @RequestParam("difficulty")String difficulty,
            @RequestParam("type")String type,
            @RequestParam("coin")Long coin,
            @RequestParam("problemStatement") String problemStatement,
            @RequestParam("testCaseFile") List<MultipartFile> multipartFiles

    ) throws IOException {
            validationService.validateProblemDetails(new ProblemDTO(title, handle, difficulty, type,
                                                                  problemStatement, multipartFiles));
            problemService.saveProblem(title,handle,difficulty,type,coin,problemStatement);
            testCaseService.saveTestCases(handle,title, multipartFiles);
            ApiResponse<String> apiResponse=ApiResponse.<String>builder()
                    .success(true)
                    .statusCode(HttpStatus.CREATED.value())
                    .message("Problem Created Successfully..!")
                    .data("Problem Created Successfully..!")
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);

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
            @RequestParam("problemStatement") String problemStatement,
            @RequestParam(value = "testCaseFile", required = false) List<MultipartFile> multipartFiles
    ) throws IOException {
//            validationService.validateProblemDetails(new ProblemDTO(title, handle, difficulty,
//                                                     type,problemStatement, multipartFiles));

            problemService.saveProblemWithId(problemId, title, handle, difficulty, type, problemStatement,coins, multipartFiles);
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

}