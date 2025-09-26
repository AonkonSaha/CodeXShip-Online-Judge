package com.judge.myojudge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TestcaseResult {

    public String status;
    public String stdout;
    @JsonProperty("expected_output")
    public String expectedOutput;
    public String stderr;

    @JsonProperty("compile_output")
    public String compileOutput;
    public String message;
    public String time;
    public String memory;
    private boolean passed;

}
