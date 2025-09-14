package com.judge.myojudge.model.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterDTO {
    String username;
    String email;
    String mobile;
    String gender;
    String password;
    @JsonProperty("confirm_pass")
    String confirmPassword;
}
