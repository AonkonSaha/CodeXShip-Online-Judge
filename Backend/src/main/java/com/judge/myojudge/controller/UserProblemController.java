package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.ProblemResponse;
import com.judge.myojudge.model.dto.ProblemSampleTcResponse;
import com.judge.myojudge.model.dto.redis.CacheProblem;
import com.judge.myojudge.model.dto.redis.CacheSampleProblem;
import com.judge.myojudge.model.mapper.ProblemMapper;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.ProblemService;
import com.judge.myojudge.service.redis.ProblemRedisService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        long start=System.currentTimeMillis();
        CacheSampleProblem cacheSampleProblem=problemRedisService.findCacheProblem(id);
//        System.out.println("Caching Problem Query: "+(System.currentTimeMillis()-start));
        ProblemSampleTcResponse problemSampleTc=null;
        if(cacheSampleProblem==null
                || cacheSampleProblem.getProblemSampleTcResponse()==null
                || cacheSampleProblem.getProblemSampleTcResponse().getSampleOutput()==null
                || cacheSampleProblem.getProblemSampleTcResponse().getSampleOutput().isEmpty()
                || cacheSampleProblem.getProblemSampleTcResponse().getSampleTestcase()==null
                || cacheSampleProblem.getProblemSampleTcResponse().getSampleTestcase().isEmpty()
                || (cacheSampleProblem.getUserEmails()!=null
                && !cacheSampleProblem.getUserEmails().contains(email)
                && !email.equals("anonymousUser")
        )
        ){
            problemSampleTc = problemService.getProblemPerPageById(
                    id,email,
                    request
            );
            if(cacheSampleProblem==null){
                cacheSampleProblem=new CacheSampleProblem();
            }
            Long solveId=null;
            if(problemSampleTc.isSolved()){
                solveId=problemSampleTc.getId();
                problemSampleTc.setSolved(false);
            }
            cacheSampleProblem.setProblemSampleTcResponse(problemSampleTc);
            cacheSampleProblem.getUserEmails().add(email);
            problemRedisService.saveCacheProblem(cacheSampleProblem);
            if(solveId!=null){
                problemRedisService.saveCacheSolvedProblem(solveId,email);
                problemSampleTc.setSolved(true);
            }
        }else{
            problemSampleTc=cacheSampleProblem.getProblemSampleTcResponse();
            Boolean is_solved=null;
            if(!email.equals("anonymousUser")){
                problemRedisService.findCacheProblemIsSolved(problemSampleTc.getId(),email);
            }
            problemSampleTc.setSolved(is_solved==null?false:true);
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
//        long start=System.currentTimeMillis();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Pageable pageable= PageRequest.of(page,size);
        CacheProblem cacheProblem=problemRedisService.findCachePaginationAndFilter(category,page,size
                ,search,difficulty,solvedFilter);
//        System.out.println("CachePaginationFilter Query: "+(System.currentTimeMillis()-start));
        Page<ProblemResponse> problemResponses;
        if(cacheProblem==null || cacheProblem.getProblemResponses()==null
                || (cacheProblem.getUserEmails()!=null
                && !cacheProblem.getUserEmails().contains(email) && !email.equals("anonymousUser"))
        ){
            problemResponses = problemService.findProblemsByCategory(request,email,category,search,difficulty,solvedFilter,pageable);
            if(cacheProblem==null){
                cacheProblem=new CacheProblem();
                cacheProblem.getUserEmails().add(email);
            }
            cacheProblem.setProblemResponses(problemResponses.getContent());
            cacheProblem.setTotalElements(problemResponses.getTotalElements());

            Set<Long> solvedIds=new HashSet<>();
            for(ProblemResponse pr:problemResponses.getContent()){
                if(pr.isSolved()){
                 solvedIds.add(pr.getId());
                 pr.setSolved(false);
                }
            }
            if(!solvedIds.isEmpty()){
                problemRedisService.saveCacheSolvedProblems(solvedIds,email);
            }

            problemRedisService.saveCachePaginationAndFilter(
                    cacheProblem,
                    category,page,size
                    ,search,difficulty,
                    solvedFilter
            );
            for(ProblemResponse problemResponse:problemResponses.getContent()){
                if(solvedIds.contains(problemResponse.getId())){
                    problemResponse.setSolved(true);
                }
            }
        }else{
            Set<Long> solvedSet = null;
            if(!email.equals("anonymousUser")){
                solvedSet=problemRedisService.findCacheSolvedProblems(email);
            }

            List<ProblemResponse> cachedProblemResponses=cacheProblem.getProblemResponses();
            if(!email.equals("anonymousUser")){
                for(ProblemResponse problemResponse: cachedProblemResponses){
                    if(solvedSet.contains(problemResponse.getId())){
                        problemResponse.setSolved(true);
                    }
                }
            }
            problemResponses= new PageImpl<>(
                    cachedProblemResponses
                    ,PageRequest.of(page,size)
                    , cacheProblem.getTotalElements()
            );

        }
//        System.out.println("Total API Times: "+(System.currentTimeMillis()-start));
        ApiResponse<Page<ProblemResponse>> apiResponse=ApiResponse.<Page<ProblemResponse>>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Problem is fetched by category("+category+")")
                .data(problemResponses)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}