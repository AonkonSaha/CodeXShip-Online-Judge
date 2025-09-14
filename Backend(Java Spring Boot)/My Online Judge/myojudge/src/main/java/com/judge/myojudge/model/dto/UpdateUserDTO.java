package com.judge.myojudge.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class UpdateUserDTO {
    @JsonProperty("user_name")
    private String userName;
    private String email;
    @JsonProperty("mobile_number")
    private String mobileNumber;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("facebook_url")
    private String facebookUrl;
    @JsonProperty("linkedin_url")
    private String linkedinUrl;
    @JsonProperty("github_url")
    private String githubUrl;
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
