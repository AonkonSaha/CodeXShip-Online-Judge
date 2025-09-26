package com.judge.myojudge.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecuteTestCase {
    String title;
    String handle;
    String input;
    String output;
}
