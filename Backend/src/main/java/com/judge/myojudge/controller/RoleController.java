package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.RoleDTO;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.service.AuthService;
import com.judge.myojudge.service.RoleService;
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
    public ResponseEntity<Void> addRole(@RequestBody RoleDTO roleDTO){
        User user=authService.fetchUserByEmail(roleDTO.getEmail());
        roleService.addRole(user,roleDTO.getRoleName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/v1/remove")
    public ResponseEntity<Void> removeRole(@RequestBody RoleDTO roleDTO){
        User user=authService.fetchUserByEmail(roleDTO.getEmail());
        roleService.deleteRole(user,roleDTO.getRoleName());
        return ResponseEntity.noContent().build();
    }

}
