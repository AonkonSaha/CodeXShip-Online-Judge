package com.judge.myojudge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    String username;
    @NotBlank(message = "Mobile or Email cannot be empty")
    @JsonProperty("mobile")
    String mobileOrEmail;
    @NotBlank(message = "Password cannot be empty")
    String password;

}
