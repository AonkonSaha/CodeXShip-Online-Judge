package com.judge.myojudge.execution_validations_code.ev_controller;

import com.judge.myojudge.execution_validations_code.ec_dto.DtoCodeSubmission;
import com.judge.myojudge.execution_validations_code.ev_model.CodeSubmission;
import com.judge.myojudge.execution_validations_code.ev_model.ExecutionResult;
import com.judge.myojudge.execution_validations_code.ev_model.TestCaseResult;
import com.judge.myojudge.execution_validations_code.ev_repo.CodeSubmissionRepository;
import com.judge.myojudge.execution_validations_code.ev_service.CodeExecutionService;
import com.judge.myojudge.model.Problem;
import com.judge.myojudge.repo.ProblemRepo;
import com.judge.myojudge.repo.TestCaseRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/code")
public class CodeExecutionController {


    private final CodeExecutionService executionService;
    private final CodeSubmissionRepository codeSubmissionRepository;
    private final ProblemRepo problemRepo;
    public CodeExecutionController(CodeExecutionService executionService, CodeSubmissionRepository codeSubmissionRepository, ProblemRepo problemRepo) {
        this.executionService = executionService;
        this.codeSubmissionRepository = codeSubmissionRepository;
        this.problemRepo = problemRepo;
    }


    @PostMapping("/submit")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String,ExecutionResult>> executeCode(@RequestBody CodeSubmission submission) {
        submission.setStatus("queue");
        ExecutionResult result = executionService.execute(submission);
        return ResponseEntity.ok(Map.of("execution",result));
    }
    @GetMapping("/submission/history/{userName}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String,List<DtoCodeSubmission>>> submissionHistory(@PathVariable String userName) {
        List<CodeSubmission> codeSubmissions=codeSubmissionRepository.findByUserName(userName);
        List<DtoCodeSubmission> dtoCodeSubmissions = new ArrayList<>();
        for(CodeSubmission codeSubmission:codeSubmissions)
        {
            DtoCodeSubmission dtoCodeSubmission=DtoCodeSubmission.builder()
                    .id(codeSubmission.getId())
                    .problemTitle(problemRepo.findById(codeSubmission.getProblemId()).orElseThrow().getTitle())
                    .language(codeSubmission.getLanguage())
                    .status(codeSubmission.getStatus())
                    .build();
            dtoCodeSubmissions.add(dtoCodeSubmission);
        }
        return ResponseEntity.ok(Map.of("execution",dtoCodeSubmissions));
    }
}
