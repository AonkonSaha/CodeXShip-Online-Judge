package com.judge.myojudge.execution_validations_code.ec_dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DtoCodeSubmission {
    private Long id;
    private String problemTitle;
    private String language;
    private String status;

}
