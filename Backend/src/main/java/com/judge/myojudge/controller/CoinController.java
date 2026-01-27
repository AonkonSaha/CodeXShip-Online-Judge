package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.UserCoinImageResponse;
import com.judge.myojudge.model.dto.UserResponse;
import com.judge.myojudge.model.mapper.UserMapper;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.AuthService;
import com.judge.myojudge.service.redis.UserRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/coins")
@RequiredArgsConstructor
public class CoinController {
    private final AuthService authService;
    private final UserMapper userMapper;
    private final UserRedisService userRedisService;

    @PreAuthorize( "hasAnyRole('ADMIN','PROBLEM_EDITOR','NORMAL_USER')")
    @GetMapping
    public ResponseEntity<ApiResponse<UserCoinImageResponse>> getCoinsWithImageUrl(){
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        UserResponse userResponse=userRedisService.findCacheUser(email);
        UserCoinImageResponse userCoinImageResponse;
        if(userResponse==null){
             userCoinImageResponse=userMapper
                    .toUserCoinImage(authService.getUserByMobileOrEmail(email));
        }else {
            userCoinImageResponse=UserCoinImageResponse.builder()
                    .totalPresentCoins(userResponse.getTotalPresentCoins())
                    .imageUrl(userResponse.getImageUrl())
                    .build();
        }
        ApiResponse<UserCoinImageResponse> apiResponse=ApiResponse.<UserCoinImageResponse>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Successfully fetched user coins and image url")
                .data(userCoinImageResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
