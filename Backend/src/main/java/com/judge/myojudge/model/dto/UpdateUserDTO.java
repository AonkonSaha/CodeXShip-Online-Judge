package com.judge.myojudge.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.judge.myojudge.validation.UniqueMobile;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class UpdateUserDTO {
    @JsonProperty("user_name")
    @Size(min = 3, max = 30, message = "Username must be between 3–30 characters")
    private String userName;

    @Email(message = "Please provide a valid email address")
    private String email;

    @JsonProperty("mobile_number")
    @NotBlank(message = "Mobile number cannot be empty")
    @Pattern(regexp = "^(?:\\+?88)?01[3-9]\\d{8}$",
            message = "Please provide a valid Bangladeshi mobile number (e.g., +8801XXXXXXXXX)")
    private String mobileNumber;

    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("facebook_url")
    private String facebookUrl;
    @JsonProperty("linkedin_url")
    private String linkedinUrl;
    @JsonProperty("github_url")
    private String githubUrl;

    @NotBlank(message = "Gender cannot be empty")
    @Pattern(regexp = "^(?i)(male|female|other)$",
            message = "Gender must be Male, Female, or Other")
    private String gender;


    private String country;
    private String state;
    private String city;
    @JsonProperty("postal_code")
    private String postalCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("birth_date")
    private LocalDate birthday;


}
