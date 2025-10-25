package com.judge.myojudge.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {
    String username;
    @NotBlank(message = "Mobile number cannot be empty")
    String mobile;
    @NotBlank(message = "Password cannot be empty")
    String password;
}
