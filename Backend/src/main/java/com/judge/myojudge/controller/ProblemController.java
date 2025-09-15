package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.ProblemDTO;
import com.judge.myojudge.model.dto.ProblemDetailWithSample;
import com.judge.myojudge.model.dto.TestcaseDTO;
import com.judge.myojudge.service.AuthService;
import com.judge.myojudge.service.ProblemService;
import com.judge.myojudge.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/problem")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;
    private final TestCaseService testCaseService;
    private final AuthService authService;

    @DeleteMapping(value="/v1/remove/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeAllProblem(
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

    @GetMapping(value="/v1/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String,ProblemDTO>>fetchOneProblem(@PathVariable String id
    ) throws IOException {
        long idd = 0;
        try {
            idd = Long.parseLong(id);

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format!");
        }
        ProblemDTO problem= problemService.fetchOneProblemByID( idd);
        return  ResponseEntity.ok(Map.of("problem",problem));
    }

    @GetMapping(value="/v1/all")
    public ResponseEntity<?>findAllProblem(
    )  {
        List<TestcaseDTO> testcaseDTOList = problemService.findProblemAll();
        return new ResponseEntity<>(testcaseDTOList,HttpStatus.OK);
    }
    @GetMapping(value="/v1/{category}")
    public ResponseEntity<?>searchAllProblemWithCategory(
            @PathVariable String category
    )  {
        List<TestcaseDTO> testcaseDTOList = problemService.findProblemAllByCategory(category);
        return new ResponseEntity<>(testcaseDTOList,HttpStatus.OK);
    }
    @GetMapping(value="/v2/{id}")
    public ResponseEntity<Map<String, ProblemDetailWithSample>>searchSingleProblem(@PathVariable String id
    )  {
        long idd = 0;
        try {
            idd = Long.parseLong(id);

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format!");
        }
        ProblemDetailWithSample problem= problemService.findProblemByID(idd);
        return ResponseEntity.ok(Map.of("problem",problem));
    }

    @PostMapping(value="/v1/save" )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createProblemDetails(
            @RequestParam("title") String title,
            @RequestParam("handle")String handle,
            @RequestParam("difficulty")String difficulty,
            @RequestParam("type")String type,
            @RequestParam("problemStatement") String problemStatement,
            @RequestParam("testCaseFile") List<MultipartFile> multipartFiles

    ) {
        try {
            if(problemService.findProblemByHandleExit(handle)) {
                return new ResponseEntity<>("This handle isnot used!", HttpStatus.NOT_ACCEPTABLE);
            }

            problemService.saveProblem(title,handle,difficulty,type,problemStatement);
            testCaseService.saveTestCases(handle,title, multipartFiles);
            return ResponseEntity.ok(Map.of("message", "Problem details created successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
    @PutMapping(value="/v1/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProblemDetails(
            @PathVariable("id") String id,
            @RequestParam("title") String title,
            @RequestParam("handle") String handle,
            @RequestParam("difficulty") String difficulty,
            @RequestParam("type") String type,
            @RequestParam("problemStatement") String problemStatement,
            @RequestParam(value = "testCaseFile", required = false) List<MultipartFile> multipartFiles
    ) {
        try {
            long problemId = Long.parseLong(id);
            problemService.saveProblemWithId(problemId, title, handle, difficulty, type, problemStatement, multipartFiles);
            return ResponseEntity.ok(Map.of("message", "Problem details updated successfully!"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid problem ID format!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to update problem details."));
        }
    }

}