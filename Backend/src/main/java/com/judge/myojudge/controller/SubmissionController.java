package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.SubmissionRequest;
import com.judge.myojudge.model.dto.SubmissionResponse;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
