package com.judge.myojudge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCoinImageResponse {
    @JsonProperty(value = "image_url")
    private String imageUrl;
    @JsonProperty("total_present_coins")
    private Long totalPresentCoins = 0L;
}
