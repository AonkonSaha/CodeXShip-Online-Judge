package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.UserResponse;
import com.judge.myojudge.model.mapper.UserMapper;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coin")
@RequiredArgsConstructor
public class CoinController {
    private final AuthService authService;
    private final UserMapper userMapper;

    @PreAuthorize( "hasAnyRole('ADMIN','NORMAL_USER')")
    @GetMapping("/get")
    public ResponseEntity<ApiResponse<UserResponse>> getCoinWithImageUrl(){
        String mobileOrEmail= SecurityContextHolder.getContext().getAuthentication().getName();
        ApiResponse<UserResponse> apiResponse=ApiResponse.<UserResponse>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Fetch User Coins")
                .data(userMapper.toUserUrlCoin(authService.getUserCoinWithImgUrl(mobileOrEmail)))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
