package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.SubmissionRequest;
import com.judge.myojudge.model.dto.SubmissionResponse;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/submission")
public class SubmissionController {
    private final SubmissionService submissionService;

    @PostMapping("/v1/submit")
    @PreAuthorize("hasAnyRole('NORMAL_USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<SubmissionResponse>> submitCode(@RequestBody @Valid SubmissionRequest submissionRequest){
        SubmissionResponse submissionResponse = submissionService.runSubmissionCode(submissionRequest);
        ApiResponse<SubmissionResponse> apiResponse=ApiResponse.<SubmissionResponse>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Submission Successfully..")
                .data(submissionResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
    @PostMapping("/v1/run/sample")
    @PreAuthorize("hasAnyRole('NORMAL_USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<SubmissionResponse>> submitSampleTestCaseCode(@RequestBody @Valid SubmissionRequest submissionRequest){
        SubmissionResponse submissionResponse = submissionService.runSampleTestCaseCode(submissionRequest);
        ApiResponse<SubmissionResponse> apiResponse=ApiResponse.<SubmissionResponse>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Sample TestCase Code Submitted Successfully..")
                .data(submissionResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }



    @GetMapping("/v1/get/user/all")
    @PreAuthorize("hasAnyRole('NORMAL_USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<SubmissionResponse>>> getSubmission(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(name = "sort_field", defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "asc") String sortBy,
            @RequestParam(name = "search", required = false) String search
    ){
        String mobileOrEmail= SecurityContextHolder.getContext().getAuthentication().getName();
        Sort sort = sortBy.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Page<SubmissionResponse> submissionResponse = submissionService.getAllSubmissionByUser(mobileOrEmail,search,sort,page,size);
        ApiResponse<Page<SubmissionResponse>> apiResponse=ApiResponse.<Page<SubmissionResponse>>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Submission Successfully..")
                .data(submissionResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }


}
