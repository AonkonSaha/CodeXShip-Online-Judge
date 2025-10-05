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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/submission")
public class SubmissionController {
    private final SubmissionService submissionService;

    @PostMapping("/v1/submit")
    @PreAuthorize("hasAnyRole('NORMAL_USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<SubmissionResponse>> submit(@RequestBody @Valid SubmissionRequest submissionRequest){
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
    public ResponseEntity<ApiResponse<Page<SubmissionResponse>>> getSubmission(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(name = "sort_field", defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "asc") String sortBy,
            @RequestParam(name = "search", required = false) String search
    ){
        String contact= SecurityContextHolder.getContext().getAuthentication().getName();
        Sort sort = sortBy.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Page<SubmissionResponse> submissionResponse = submissionService.getAllSubmissionByUser(contact,search,sort,page,size);
        ApiResponse<Page<SubmissionResponse>> apiResponse=ApiResponse.<Page<SubmissionResponse>>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Submission Successfully..")
                .data(submissionResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }


}
