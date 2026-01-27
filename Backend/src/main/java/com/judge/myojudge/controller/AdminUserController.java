package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.UserResponse;
import com.judge.myojudge.model.dto.UserUpdateRequest;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.model.mapper.UserMapper;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.AuthService;
import com.judge.myojudge.service.redis.UserRedisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final AuthService authService;
    private final UserMapper userMapper;
    private final UserRedisService userRedisService;
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String search
    ){
        Pageable pageable = PageRequest.of(page,size);
        Page<User> users = authService.getUsers(search,pageable);
        List<UserResponse> usersResponse = userMapper.toUsersResponse(users.getContent());
        ApiResponse<Page<UserResponse>> apiResponse= ApiResponse.<Page<UserResponse>>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Fetched Users Successfully")
                .data(new PageImpl<>(usersResponse, pageable,users.getTotalElements()))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateUser(@RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        authService.updateUserDetailsByAdmin(userUpdateRequest);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String email){
        authService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }

}
