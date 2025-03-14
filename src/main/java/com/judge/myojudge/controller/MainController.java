package com.judge.myojudge.controller;

import com.judge.myojudge.dto.ProblemDetailWithSample;
import com.judge.myojudge.dto.ProblemWithTestCases;
import com.judge.myojudge.dto.UserLogin;
import com.judge.myojudge.dto.UserRegister;
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

    @GetMapping(value="/hellow")
    public String hellowWorld()
    {
      return "I am a Public";
    }

    @GetMapping(value="/problems/all")
    public ResponseEntity<?>searchAllProblem(
    )  {
        List<ProblemWithTestCases> problemWithTestCasesList=problemService.findProblemAll();
        return new ResponseEntity<>(problemWithTestCasesList,HttpStatus.OK);
    }
    @GetMapping(value="/problems/category/{category}")
    public ResponseEntity<?>searchAllProblemWithCategory(
            @PathVariable String category
    )  {
        List<ProblemWithTestCases> problemWithTestCasesList=problemService.findProblemAllByCategory(category);
        return new ResponseEntity<>(problemWithTestCasesList,HttpStatus.OK);
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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegister userRegister) {
        return ResponseEntity.ok(authService.register(userRegister));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody UserLogin userLogin) {
        return  ResponseEntity.ok(Map.of("token",authService.login(userLogin)));
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        System.out.println("LOGOUT");
        return ResponseEntity.ok(authService.logout(request));
    }

}


