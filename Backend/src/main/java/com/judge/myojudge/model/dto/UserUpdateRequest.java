package com.judge.myojudge.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class UserUpdateRequest {
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 30, message = "Username must be between 3–30 characters")
    String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email address")
    String email;

    @Pattern(regexp = "^(?:\\+?88)?01[3-9]\\d{8}$",
            message = "Please provide a valid Bangladeshi mobile number (e.g., +8801XXXXXXXXX)")
    String mobile;

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

    private Long coins=0L;
    @JsonProperty("total_coins_earned")
    private Long totalCoinsEarned = 0L;
    @JsonProperty("total_coins_expend")
    private Long totalCoinsExpend = 0L;
    @JsonProperty("total_present_coins")
    private Long totalPresentCoins = 0L;
    @JsonProperty("total_problems_solved")
    private Long totalProblemsSolved = 0L;
    @JsonProperty("total_problems_attempted")
    private Long totalProblemsAttempted = 0L;
    @JsonProperty("total_problems_failed")
    private Long totalProblemsFailed = 0L;
    @JsonProperty("total_problems_pending")
    private Long totalProblemsPending = 0L;
    @JsonProperty("total_problems_tle")
    private Long totalProblemsTLE = 0L;
    @JsonProperty("total_problems_re")
    private Long totalProblemsRE = 0L;
    @JsonProperty("total_problems_wa")
    private Long totalProblemsWA = 0L;
    @JsonProperty("total_problems_ce")
    private Long totalProblemsCE = 0L;

    private String country;
    private String state;
    private String city;
    @JsonProperty("postal_code")
    private String postalCode;
    @JsonProperty("activity_status")
    private boolean activityStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("date_of_birth")
    @Past(message = "Birthdate can't be future")
    private LocalDate birthday;
}
