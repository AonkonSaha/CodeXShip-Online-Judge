package com.judge.myojudge.controller;

import com.judge.myojudge.dto.ProblemDTO;
import com.judge.myojudge.model.Problem;
import com.judge.myojudge.service.AuthService;
import com.judge.myojudge.service.ProblemService;
import com.judge.myojudge.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProblemService problemService;
    private final TestCaseService testCaseService;
    @Autowired
    private AuthService authService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard() {
        return "Welcome to the Admin Dashboard!";
    }

    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public String Test() {
        return "I am a Admin!";
    }

    @DeleteMapping(value="/problems/remove/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeAllProblem(
    ) throws IOException {
        String  message=problemService.deleteEachProblem();
        return  ResponseEntity.ok().body(message);
    }
    @DeleteMapping(value="/problems/remove/{handle}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?>removeProblem(@PathVariable String handle
    ) throws IOException {
        return  ResponseEntity.ok().body(problemService.deleteProblemByHandle( handle));
    }

    @GetMapping(value="/problem/{id}")
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

    @PostMapping(value="/problem/save" )
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
    @PutMapping(value="/problem/update/{id}")
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


//    @PutMapping(value="/problem/update/{id}" )
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> updateProblemDetails(
//            @PathVariable("id") String id,
//            @RequestParam("title") String title,
//            @RequestParam("handle")String handle,
//            @RequestParam("difficulty")String difficulty,
//            @RequestParam("type")String type,
//            @RequestParam("problemStatement") String problemStatement,
//            @RequestParam("testCaseFile") List<MultipartFile> multipartFiles
//
//    ) throws IOException {
//            long idd=0;
//        System.out.println("Number format: "+id);
//            try
//            {
//                idd = Long.parseLong(id);
//            }catch (NumberFormatException e) {
//                System.out.println("Invalid number format!");
//            }
//
//            problemService.saveProblemWithId(idd,title,handle,difficulty,type,problemStatement,multipartFiles);
//            return ResponseEntity.ok(Map.of("message", "Problem details updated successfully!"));
//
//    }
}