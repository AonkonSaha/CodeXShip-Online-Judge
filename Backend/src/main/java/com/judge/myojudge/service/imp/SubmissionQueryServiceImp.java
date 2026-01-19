package com.judge.myojudge.service.imp;

import com.judge.myojudge.model.entity.Submission;
import com.judge.myojudge.repository.SubmissionRepo;
import com.judge.myojudge.service.SubmissionQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class SubmissionQueryServiceImp implements SubmissionQueryService {
    private final SubmissionRepo submissionRepo;


    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public List<Submission> getSubmissionsByUserWithAccepted(String mobileOrEmail, String handle, String accept) {
        return submissionRepo.findByMobileOrEmailAndHandleAndStatus(mobileOrEmail,handle,"Accepted");

    }
}
