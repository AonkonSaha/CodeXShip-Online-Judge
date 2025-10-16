package com.judge.myojudge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private String username;
    private String email;
    @JsonProperty("mobile_number")
    private String mobileNumber;
    private String password;
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

    @JsonProperty("image_url")
    private String imageUrl;
    private String gender;
    private String country;
    private String state;
    private String city;
    @JsonProperty("postal_code")
    private String postalCode;

    @JsonProperty("fb_url")
    private String facebookUrl;
    @JsonProperty("linkedin_url")
    private String linkedinUrl;
    @JsonProperty("github_url")
    private String githubUrl;

    @JsonProperty("activity_status")
    private Boolean activityStatus;
    @JsonProperty("total_coins_earned")
    private Long TotalCoinsEarned = 0L;
    @JsonProperty("total_coins_expend")
    private Long TotalCoinsExpend = 0L;
    @JsonProperty("total_present_coins")
    private Long TotalPresentCoins = 0L;
    @JsonProperty("total_problems_solved")
    private Long totalProblemsSolved;
    @JsonProperty("total_problems_attempted")
    private Long totalProblemsAttempted;
    @JsonProperty("total_problems_failed")
    private Long totalProblemsFailed;
    @JsonProperty("total_problems_pending")
    private Long totalProblemsPending;
    @JsonProperty("total_problems_tle")
    private Long totalProblemsTLE;
    @JsonProperty("total_problems_re")
    private Long totalProblemsRE;
    @JsonProperty("total_problems_wa")
    private Long totalProblemsWA;
    @JsonProperty("total_problems_ce")
    private Long totalProblemsCE;

}
