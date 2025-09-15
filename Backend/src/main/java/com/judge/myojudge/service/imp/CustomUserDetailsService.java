package com.judge.myojudge.service.imp;

import com.judge.myojudge.model.entity.UserRole;
import com.judge.myojudge.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import com.judge.myojudge.model.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String mobile) throws UsernameNotFoundException {
         User user = userRepo.findByMobileNumberWithRoles(mobile)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        for (UserRole userRole : user.getUserRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.getRoleName()));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getMobileNumber(),
                user.getPassword(),
                authorities

        );
    }
}