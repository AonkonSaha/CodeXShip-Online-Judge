package com.judge.myojudge.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordDTO {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
