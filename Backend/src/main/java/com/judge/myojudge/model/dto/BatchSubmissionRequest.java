package com.judge.myojudge.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchSubmissionRequest {

    private boolean base64_encoded;
    private boolean wait;
    private List<Map<String, Object>> submissions;
}
