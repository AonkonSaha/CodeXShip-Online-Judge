package com.judge.myojudge.controller;

import com.judge.myojudge.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/submissions")
@RequiredArgsConstructor
public class AdminSubmissionController {
    private final SubmissionService submissionService;

    @DeleteMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteSubmissionsPerUser(
            @RequestParam("mobile_or_email") String mobileOrEmail
    ){
        submissionService.deleteAllSubmissionByUser(mobileOrEmail);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
