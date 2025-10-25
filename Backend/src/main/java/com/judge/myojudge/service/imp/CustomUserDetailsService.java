package com.judge.myojudge.service.imp;

import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.model.entity.UserRole;
import com.judge.myojudge.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    @Transactional
    public UserDetails loadUserByUsername(String mobile) {
         User user = userRepo.findByMobileNumber(mobile)
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