package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.ProblemResponse;
import com.judge.myojudge.model.dto.ProblemSampleTcResponse;
import com.judge.myojudge.model.mapper.ProblemMapper;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.ProblemService;
import com.judge.myojudge.service.redis.ProblemRedisService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/problems")
@RequiredArgsConstructor
public class UserProblemController {

    private final ProblemService problemService;
    private final ProblemRedisService problemRedisService;
    private final ProblemMapper problemMapper;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProblemSampleTcResponse>>getProblemForPage(@PathVariable Long id
    , HttpServletRequest request) throws IOException {
        String mobileOrEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ProblemSampleTcResponse problemSampleTc=problemRedisService.findCacheProblem(id,mobileOrEmail);
        if(problemSampleTc==null){
            System.out.println("KKKK");
            problemSampleTc = problemService.getProblemPerPageById(
                    id,mobileOrEmail,
                    request
            );
            problemRedisService.saveCacheProblem(problemSampleTc,mobileOrEmail);
        }

        ApiResponse<ProblemSampleTcResponse> apiResponse=ApiResponse.<ProblemSampleTcResponse>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Problem is fetched successfully.")
                .data(problemSampleTc)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }


    @GetMapping(value="/category/{category}")
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
}