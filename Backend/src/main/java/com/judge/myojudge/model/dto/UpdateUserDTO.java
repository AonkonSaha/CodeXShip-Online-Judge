package com.judge.myojudge.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class UpdateUserDTO {
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 30, message = "Username must be between 3–30 characters")
    String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email address")
    String email;

    @NotBlank(message = "Mobile number cannot be empty")
    @Pattern(regexp = "^(?:\\+?88)?01[3-9]\\d{8}$",
            message = "Please provide a valid Bangladeshi mobile number (e.g., +8801XXXXXXXXX)")
    String mobile;

    @NotBlank(message = "Gender cannot be empty")
    @Pattern(regexp = "^(?i)(male|female|other)$",
            message = "Gender must be Male, Female, or Other")
    String gender;

    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("facebook_url")
    private String facebookUrl;
    @JsonProperty("linkedin_url")
    private String linkedinUrl;
    @JsonProperty("github_url")
    private String githubUrl;

    private String country;
    private String state;
    private String city;
    @JsonProperty("postal_code")
    private String postalCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("date_of_birth")
    @Past(message = "Birthdate can't be future")
    private LocalDate birthday;


}
