package com.judge.myojudge.service.imp;

import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.model.entity.UserRole;
import com.judge.myojudge.repository.RoleRepo;
import com.judge.myojudge.repository.UserRepo;
import com.judge.myojudge.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class RoleServiceImp implements RoleService {
    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    @Override
    @CacheEvict(cacheNames = {"userDetails","CoinWithImg","users","user"}, allEntries = true)
    @Transactional(value = Transactional.TxType.REQUIRED)
    public void addRole(User user, String role) {
        UserRole userRole=new UserRole();
        userRole.setRoleName(role);
        userRole.setUsers(Set.of(user));
        Set<UserRole> userRoles = user.getUserRoles();
        userRoles.add(userRole);
        user.setUserRoles(userRoles);
        userRepo.save(user);
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRED)
    @CacheEvict(cacheNames = {"userDetails","CoinWithImg","users","user"}, allEntries = true)
    public void deleteRole(User user, String roleName) {
        for (UserRole userRole : user.getUserRoles()) {
            if (userRole.getRoleName().equals(roleName)) {
                user.getUserRoles().remove(userRole);
                roleRepo.delete(userRole);
                userRepo.save(user);
                return;
            }
        }
    }
}
