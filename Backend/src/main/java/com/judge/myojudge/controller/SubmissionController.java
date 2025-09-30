package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.SubmissionRequest;
import com.judge.myojudge.model.dto.SubmissionResponse;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/submission")
public class SubmissionController {
    private final SubmissionService submissionService;

    @PostMapping("/v1/submit")
    @PreAuthorize("hasAnyRole('NORMAL_USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<SubmissionResponse>> submit(@RequestBody SubmissionRequest submissionRequest){
        SubmissionResponse submissionResponse = submissionService.excuteCode(submissionRequest);
        ApiResponse<SubmissionResponse> apiResponse=ApiResponse.<SubmissionResponse>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Submission Successfully..")
                .data(submissionResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/v1/get/user/all")
    @PreAuthorize("hasAnyRole('NORMAL_USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Set<SubmissionResponse>>> getSubmission(){
        String contact= SecurityContextHolder.getContext().getAuthentication().getName();
        Set<SubmissionResponse> submissionResponse = submissionService.getAllSubmissionByUser(contact);
        ApiResponse<Set<SubmissionResponse>> apiResponse=ApiResponse.<Set<SubmissionResponse>>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Submission Successfully..")
                .data(submissionResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }


}
