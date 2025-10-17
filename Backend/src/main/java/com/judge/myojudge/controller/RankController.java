package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.UserDTO;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.model.mapper.UserMapper;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.RankService;
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
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankController {
    private final RankService rankService;
    private final UserMapper userMapper;
    @GetMapping("/v1/get")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getRanking(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String search
    ){
        Pageable pageable = PageRequest.of(page,size);
        Page<User> users = rankService.getRanking(search,pageable);
        List<UserDTO> userDTOS= userMapper.toUserDTO(users.getContent());
        return ResponseEntity.ok(ApiResponse.<Page<UserDTO>>builder()
                .success(true)
                .statusCode(200)
                .message("Ranking Fetched Successfully")
                .data(new PageImpl<>(userDTOS.stream().toList(), pageable,users.getTotalElements()))
                .build());
    }

}
