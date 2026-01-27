package com.judge.myojudge.service.imp;

import com.judge.myojudge.model.dto.redis.CacheUserAuth;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.model.entity.UserRole;
import com.judge.myojudge.model.mapper.UserMapper;
import com.judge.myojudge.service.AuthService;
import com.judge.myojudge.service.redis.UserRedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AuthService authService;
    private final UserRedisService userRedisService;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) {

        CacheUserAuth cacheUserAuth=userRedisService.findCacheUserAuth(email);
        List<String> roleNames;
        String password;

        if(cacheUserAuth==null){
            User user = authService.getUserByMobileOrEmail(email);
            roleNames=new ArrayList<>();
                for(UserRole userRole:user.getUserRoles()){
                    roleNames.add(userRole.getRoleName());
                }
            password=user.getPassword();
            CacheUserAuth requestCache=CacheUserAuth.builder()
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .roleNames(roleNames)
                    .build();

            userRedisService.saveCacheUserAuth(requestCache);
        }else{
            roleNames=cacheUserAuth.getRoleNames();
            password=cacheUserAuth.getPassword();
        }
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        for (String roleName : roleNames){
            authorities.add(new SimpleGrantedAuthority("ROLE_" +roleName));
        }
        return new org.springframework.security.core.userdetails.User(
                email,
                password,
                authorities

        );
    }
}