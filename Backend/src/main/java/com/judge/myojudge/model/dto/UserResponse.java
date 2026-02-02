package com.judge.myojudge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long userId; //use only send it as a response
    private String username;
    private String email;
    private String mobile;
    private String password;
    private List<Long> userSolvedProblems;
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;
    @JsonProperty("created_time")
    private LocalDateTime createdTime;
    @JsonProperty("updated_time")
    private LocalDateTime updateTime;

    @JsonProperty("activity_status")
    private boolean activityStatus;
    @JsonProperty("is_google_user")
    private boolean isGoogleUser=false;
    @JsonProperty("image_url")
    private String imageUrl;
    private String gender;
    private String country;
    private String state;
    private String city;
    @JsonProperty("postal_code")
    private String postalCode;

    @JsonProperty("facebook_url")
    private String facebookUrl;
    @JsonProperty("linkedin_url")
    private String linkedinUrl;
    @JsonProperty("github_url")
    private String githubUrl;
    private Set<String> roles;
    @JsonProperty("total_coins_earned")
    private Long totalCoinsEarned = 0L;
    @JsonProperty("total_coins_expend")
    private Long totalCoinsExpend = 0L;
    @JsonProperty("total_present_coins")
    private Long totalPresentCoins = 0L;
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
