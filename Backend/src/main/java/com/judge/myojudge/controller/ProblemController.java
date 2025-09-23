package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.*;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.AuthService;
import com.judge.myojudge.service.ProblemService;
import com.judge.myojudge.service.TestCaseService;
import com.judge.myojudge.validation.ValidationService;
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
    private final ValidationService validationService;

    @GetMapping(value="/v1/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProblemDTO>>fetchOneProblem(@PathVariable String id
    ) throws IOException {
        long problemId = Long.parseLong(id);
        ProblemDTO problemDTO= problemService.fetchOneProblemByID(problemId);

        ApiResponse<ProblemDTO> apiResponse= ApiResponse.<ProblemDTO>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Fetching One Problem Success..!")
                .data(problemDTO)
                .build();

        return  ResponseEntity.status(HttpStatus.OK).body(apiResponse);
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
    public ResponseEntity<?>searchAllProblemWithCategory(
            @PathVariable String category
    )  {
        List<ProblemWithSample> problemWithSamples = problemService.findProblemAllByCategory(category);
        ApiResponse<List<ProblemWithSample>> problemWithSample=ApiResponse.<List<ProblemWithSample>>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Fetching Problems With Category Successfully..!")
                .data(problemWithSamples)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(problemWithSample);
    }
    @GetMapping(value="/v2/{id}")
    public ResponseEntity<Map<String, ProblemDetailWithSample>>searchSingleProblem(@PathVariable String id
    ) {
        long problemId = Long.parseLong(id);
        ProblemDetailWithSample problem= problemService.findProblemByID(problemId);
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

    ) throws IOException {

            validationService.validateProblemDetails(new ProblemDTO(title, handle, difficulty, type,
                                                                  problemStatement, multipartFiles));
            problemService.saveProblem(title,handle,difficulty,type,problemStatement);
            testCaseService.saveTestCases(handle,title, multipartFiles);
            return ResponseEntity.ok(Map.of("message", "Problem details created successfully!"));

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
            validationService.validateProblemDetails(new ProblemDTO(title, handle, difficulty, type,
                    problemStatement, multipartFiles));

            long problemId = Long.parseLong(id);
            problemService.saveProblemWithId(problemId, title, handle, difficulty, type, problemStatement, multipartFiles);
            return ResponseEntity.ok(Map.of("message", "Problem details updated successfully!"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid problem ID format!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to update problem details."));
        }
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