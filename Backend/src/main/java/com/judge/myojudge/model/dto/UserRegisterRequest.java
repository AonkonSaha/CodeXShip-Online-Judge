package com.judge.myojudge.model.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.judge.myojudge.validation.PasswordMatches;
import com.judge.myojudge.validation.UniqueEmail;
import com.judge.myojudge.validation.UniqueMobile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@PasswordMatches
public class UserRegisterRequest {
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 30, message = "Username must be between 3–30 characters")
    private String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email address")
    @UniqueEmail(message = "This email is already registered")
    private String email;

    @NotBlank(message = "Mobile number cannot be empty")
    @UniqueMobile(message = "This mobile number is already registered")
    @Pattern(regexp = "^(?:\\+?88)?01[3-9]\\d{8}$",
            message = "Please provide a valid Bangladeshi mobile number (e.g., +8801XXXXXXXXX)")
    private String mobile;

    @NotBlank(message = "Gender cannot be empty")
    @Pattern(regexp = "^(?i)(male|female|other)$",
            message = "Gender must be Male, Female, or Other")
    private String gender;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, max = 64, message = "Password must be 8–64 characters long")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;


    @JsonProperty("confirm_pass")
    @NotBlank(message = "Confirm Password cannot be empty")
    @Size(min = 8, max = 64, message = "Confirm Password must be 8–64 characters long")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Confirm Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String confirmPassword;
}
