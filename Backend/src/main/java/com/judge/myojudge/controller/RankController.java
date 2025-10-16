package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.UserDTO;
import com.judge.myojudge.model.mapper.UserMapper;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankController {
    private final RankService rankService;
    private final UserMapper userMapper;
    @RequestMapping("/v1/get")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getRanking(){
        List<UserDTO> userDTOS= userMapper.toUserDTO(rankService.getRanking());
        return ResponseEntity.ok(ApiResponse.<List<UserDTO>>builder()
                .success(true)
                .statusCode(200)
                .message("Ranking Fetched Successfully")
                .data(userDTOS)
                .build());
    }

}
