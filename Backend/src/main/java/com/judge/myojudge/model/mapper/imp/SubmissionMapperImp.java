package com.judge.myojudge.model.mapper.imp;

import com.judge.myojudge.model.dto.SubmissionResponse;
import com.judge.myojudge.model.entity.Submission;
import com.judge.myojudge.model.mapper.SubmissionMapper;
import com.judge.myojudge.model.mapper.TestCaseMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubmissionMapperImp implements SubmissionMapper {
    private final TestCaseMapper testCaseMapper;
    @Override
    @Transactional(value = Transactional.TxType.SUPPORTS)
    public SubmissionResponse toSubmissionResponse(Submission submission) {
        return SubmissionResponse.builder()
                .id(submission.getId())
                .problemId(submission.getProblem().getId())
                .problemName(submission.getProblem().getTitle())
                .language(submission.getLanguage())
                .verdict(submission.getStatus())
                .passed(submission.getPassedTestcases())
                .total(submission.getTotalTestcases())
                .time(submission.getTime())
                .memory(submission.getMemory())
                .createdAt(submission.getCreatedAt())
                .build();
    }

    @Override
    public Submission toSubmission(SubmissionResponse submissionResponse) {
        return null;
    }

    @Override
    @Transactional(value = Transactional.TxType.SUPPORTS)
    public SubmissionResponse toSubmissionResponseWithCode(Submission submission) {
        return SubmissionResponse.builder()
                .id(submission.getId())
                .problemId(submission.getProblem().getId())
                .problemName(submission.getProblem().getTitle())
                .language(submission.getLanguage())
                .verdict(submission.getStatus())
                .passed(submission.getPassedTestcases())
                .total(submission.getTotalTestcases())
                .time(submission.getTime())
                .memory(submission.getMemory())
                .createdAt(submission.getCreatedAt())
                .submissionCode(submission.getUserCode())
                .results(submission.getTestCaseResults().stream().map(testCaseResult -> {
                    return testCaseMapper.toTestCaseResponse(testCaseResult);
                }).toList())
                .build();
    }
}
