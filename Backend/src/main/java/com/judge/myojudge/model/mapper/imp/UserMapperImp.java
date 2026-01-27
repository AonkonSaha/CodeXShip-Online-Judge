package com.judge.myojudge.model.mapper.imp;

import com.judge.myojudge.enums.Role;
import com.judge.myojudge.model.dto.UserCoinImageResponse;
import com.judge.myojudge.model.dto.UserRegisterRequest;
import com.judge.myojudge.model.dto.UserRegisterResponse;
import com.judge.myojudge.model.dto.UserResponse;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.model.entity.UserRole;
import com.judge.myojudge.model.mapper.UserMapper;
import com.judge.myojudge.repository.SubmissionRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapperImp implements UserMapper {
    private final PasswordEncoder passwordEncoder;
    private final SubmissionRepo submissionRepo;

    @Override
    public User toUser(UserRegisterRequest userRegisterRequest) {
        UserRole userRole = new UserRole();
        userRole.setRoleName(Role.NORMAL_USER.name());
        User user= User.builder()
               .username(userRegisterRequest.getUsername())
               .email(userRegisterRequest.getEmail())
                .gender(userRegisterRequest.getGender())
               .mobileNumber(userRegisterRequest.getMobile())
               .userRoles(Set.of(userRole))
               .password(passwordEncoder.encode(userRegisterRequest.getPassword()))
               .build();
        userRole.setUsers(Set.of(user));
        return user;
    }

    @Override
    public UserRegisterResponse toUserRegisterResponse(User user) {
        return UserRegisterResponse.builder()
                .username(user.getUsername())
                .mobile(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Override
    @Transactional(value = Transactional.TxType.SUPPORTS)
    public List<UserResponse> toUsersResponse(List<User> users) {
        List<UserResponse> userResponses =new ArrayList<>();
        for(User user:users){
            UserResponse userResponse = UserResponse.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .country(user.getCountry())
                    .mobile(user.getMobileNumber())
                    .imageUrl(user.getImageUrl()==null?"":user.getImageUrl())
                    .activityStatus(user.getActivityStatus() != null && user.getActivityStatus())
                    .email(user.getEmail())
                    .city(user.getCity())
                    .postalCode(user.getPostalCode())
                    .state(user.getState())
                    .createdTime(user.getCreatedAt())
                    .updateTime(user.getUpdatedAt() == null ? user.getCreatedAt() : user.getUpdatedAt())
                    .gender(user.getGender())
                    .dateOfBirth(user.getDateOfBirth())
                    .facebookUrl(user.getFacebookUrl())
                    .githubUrl(user.getGithubUrl())
                    .linkedinUrl(user.getLinkedinUrl())
                    .totalProblemsAttempted(user.getTotalProblemsAttempted())
                    .totalProblemsCE(user.getTotalProblemsCE())
                    .totalProblemsRE(user.getTotalProblemsRE())
                    .totalProblemsTLE(user.getTotalProblemsTLE())
                    .totalProblemsWA(user.getTotalProblemsWA())
                    .totalCoinsEarned(user.getTotalCoinsEarned() == null ? 0 : user.getTotalCoinsEarned())
                    .totalCoinsExpend(user.getTotalCoinsExpend() == null ? 0 : user.getTotalCoinsExpend())
                    .totalPresentCoins(user.getTotalPresentCoins())
                    .totalProblemsSolved(user.getTotalProblemsSolved())
                    .roles(user.getUserRoles()
                            .stream()
                            .map(userRole->userRole.getRoleName())
                            .collect(Collectors.toSet())
                    )
                    .isGoogleUser(user.getIsGoogleUser()==null?false:user.getIsGoogleUser())
                    .build();
            userResponses.add(userResponse);
        }
        return userResponses;
    }

    @Override
    public UserCoinImageResponse toUserCoinImage(User user) {
        return UserCoinImageResponse.builder()
                .imageUrl(user.getImageUrl())
                .totalPresentCoins(user.getTotalPresentCoins())
                .build();
    }

    @Override
    @Transactional(value = Transactional.TxType.SUPPORTS)
    public UserResponse toUserResponse(User user) {
        return  UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .country(user.getCountry())
                .mobile(user.getMobileNumber())
                .imageUrl(user.getImageUrl()==null?"":user.getImageUrl())
                .activityStatus(user.getActivityStatus() != null && user.getActivityStatus())
                .email(user.getEmail())
                .city(user.getCity())
                .postalCode(user.getPostalCode())
                .state(user.getState())
                .createdTime(user.getCreatedAt())
                .updateTime(user.getUpdatedAt() == null ? user.getCreatedAt() : user.getUpdatedAt())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .facebookUrl(user.getFacebookUrl())
                .githubUrl(user.getGithubUrl())
                .linkedinUrl(user.getLinkedinUrl())
                .totalProblemsAttempted(user.getTotalProblemsAttempted())
                .totalProblemsCE(user.getTotalProblemsCE())
                .totalProblemsRE(user.getTotalProblemsRE())
                .totalProblemsTLE(user.getTotalProblemsTLE())
                .totalProblemsWA(user.getTotalProblemsWA())
                .totalCoinsEarned(user.getTotalCoinsEarned() == null ? 0 : user.getTotalCoinsEarned())
                .totalCoinsExpend(user.getTotalCoinsExpend() == null ? 0 : user.getTotalCoinsExpend())
                .totalPresentCoins(user.getTotalPresentCoins())
                .totalProblemsSolved(user.getTotalProblemsSolved())
                .isGoogleUser(user.getIsGoogleUser()==null?false:user.getIsGoogleUser())
                .build();
    }

}
