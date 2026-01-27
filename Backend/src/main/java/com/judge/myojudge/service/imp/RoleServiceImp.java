package com.judge.myojudge.service.imp;

import com.judge.myojudge.model.dto.redis.CacheUserAuth;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.model.entity.UserRole;
import com.judge.myojudge.repository.RoleRepo;
import com.judge.myojudge.repository.UserRepo;
import com.judge.myojudge.service.RoleService;
import com.judge.myojudge.service.redis.UserRedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RoleServiceImp implements RoleService {
    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final UserRedisService userRedisService;
    @Override
    @Transactional(value = Transactional.TxType.REQUIRED)
    public void addRole(User user, String role) {
        UserRole userRole=new UserRole();
        userRole.setRoleName(role);
        userRole.setUsers(Set.of(user));
        Set<UserRole> userRoles = user.getUserRoles();
        userRoles.add(userRole);
        user.setUserRoles(userRoles);
        userRepo.save(user);
        List<String> roleNames=new ArrayList<>();
        for(UserRole cacheRole:user.getUserRoles()){
            roleNames.add(cacheRole.getRoleName());
        }
        CacheUserAuth cacheRequest=CacheUserAuth.builder()
                .roleNames(roleNames)
                .password(user.getPassword())
                .email(user.getEmail())
                .build();
        userRedisService.updateCacheUserAuth(cacheRequest);
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRED)
    public void deleteRole(User user, String roleName) {
        for (UserRole userRole : user.getUserRoles()) {
            if (userRole.getRoleName().equals(roleName)) {
                user.getUserRoles().remove(userRole);
                roleRepo.delete(userRole);
                userRepo.save(user);
                userRedisService.deleteCacheUserAuthRole(user.getEmail(),userRole.getRoleName());
                return;
            }
        }
    }
}
