package com.judge.myojudge.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProblemDetailWithSample {
    Long id;
    String  name;
    String statement;
    List<String> input;
    List<String>output;
    String explanation;
    String difficulty;
    Boolean solve;

}
