package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.ProblemDetailWithSample;
import com.judge.myojudge.model.dto.TestcaseDTO;
import com.judge.myojudge.model.dto.LoginDTO;
import com.judge.myojudge.model.dto.RegisterDTO;
import com.judge.myojudge.service.AuthService;
import com.judge.myojudge.service.ProblemService;
import com.judge.myojudge.service.TestCaseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MainController {

    @Autowired
    ProblemService problemService;
    @Autowired
    TestCaseService testCaseService;
    @Autowired
    private AuthService authService;


    @GetMapping(value="/problems/all")
    public ResponseEntity<?>searchAllProblem(
    )  {
        List<TestcaseDTO> testcaseDTOList =problemService.findProblemAll();
        return new ResponseEntity<>(testcaseDTOList,HttpStatus.OK);
    }
    @GetMapping(value="/problems/category/{category}")
    public ResponseEntity<?>searchAllProblemWithCategory(
            @PathVariable String category
    )  {
        List<TestcaseDTO> testcaseDTOList =problemService.findProblemAllByCategory(category);
        return new ResponseEntity<>(testcaseDTOList,HttpStatus.OK);
    }
    @GetMapping(value="/problems/{id}")
    public ResponseEntity<Map<String, ProblemDetailWithSample>>searchSingleProblem(@PathVariable String id
    )  {
        long idd = 0;
        try {
             idd = Long.parseLong(id);

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format!");
        }
        ProblemDetailWithSample problem=problemService.findProblemByID(idd);
        return ResponseEntity.ok(Map.of("problem",problem));
    }

}


