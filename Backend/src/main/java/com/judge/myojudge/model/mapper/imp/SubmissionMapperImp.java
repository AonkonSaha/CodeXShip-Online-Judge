package com.judge.myojudge.model.mapper.imp;

import com.judge.myojudge.model.dto.SubmissionResponse;
import com.judge.myojudge.model.entity.Submission;
import com.judge.myojudge.model.mapper.SubmissionMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class SubmissionMapperImp implements SubmissionMapper {
    @Override
    @Transactional(value = Transactional.TxType.REQUIRED)
    public SubmissionResponse toSubmissionResponse(Submission submission) {
        return SubmissionResponse.builder()
                .id(submission.getId())
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
}
