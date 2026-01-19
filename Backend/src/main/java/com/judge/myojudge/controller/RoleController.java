package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.RoleRequest;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.service.AuthService;
import com.judge.myojudge.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final AuthService authService;

    @PostMapping("/v1/register")
    @Transactional
    public ResponseEntity<Void> addRole(@RequestBody RoleRequest roleRequest){
        User user=authService.fetchUserByEmail(roleRequest.getEmail());
        roleService.addRole(user, roleRequest.getRoleName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/v1/remove")
    @Transactional
    public ResponseEntity<Void> removeRole(@RequestBody RoleRequest roleRequest){
        User user=authService.fetchUserByEmail(roleRequest.getEmail());
        roleService.deleteRole(user, roleRequest.getRoleName());
        return ResponseEntity.noContent().build();
    }

}
