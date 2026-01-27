package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.UserResponse;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.model.mapper.UserMapper;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.RankService;
import com.judge.myojudge.service.redis.RankRedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ranking")
@RequiredArgsConstructor
public class RankController {
    private final RankService rankService;
    private final UserMapper userMapper;
    private final RankRedisService rankRedisService;

    @GetMapping
    @Transactional
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getRanking(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search
    ){
        Pageable pageable = PageRequest.of(page,size);
        Page<User> users = rankService.getRanking(search,pageable);
        List<UserResponse> userResponses=userMapper.toUsersResponse(users.getContent());

        return ResponseEntity.ok(ApiResponse.<Page<UserResponse>>builder()
                .success(true)
                .statusCode(200)
                .message("User Ranking List Retrieved Successfully")
                .data(new PageImpl<>(userResponses, pageable,users.getTotalElements()))
                .build());
    }

}
