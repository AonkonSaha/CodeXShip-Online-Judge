package com.judge.myojudge.service;

import com.judge.myojudge.model.entity.User;

public interface RoleService {
    public void addRole(User user, String role);
    void deleteRole(User user, String roleName);
}
