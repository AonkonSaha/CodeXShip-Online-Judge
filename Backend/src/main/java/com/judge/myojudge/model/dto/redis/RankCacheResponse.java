package com.judge.myojudge.model.dto.redis;

import com.judge.myojudge.model.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RankCacheResponse {

    private List<Long> userIds;
    private Long totalElements;
    private List<UserResponse> userResponses;
}
