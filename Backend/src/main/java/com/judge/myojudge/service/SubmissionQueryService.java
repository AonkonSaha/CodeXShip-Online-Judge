package com.judge.myojudge.service;

import com.judge.myojudge.model.entity.Submission;

import java.util.List;

public interface SubmissionQueryService {

    public List<Submission> getSubmissionsByUserWithAccepted(String mobileOrEmail,String handle,String accept);
}
