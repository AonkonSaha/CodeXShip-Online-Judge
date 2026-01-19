package com.judge.myojudge.model.mapper.imp;

import com.judge.myojudge.enums.Role;
import com.judge.myojudge.model.dto.UserRegisterRequest;
import com.judge.myojudge.model.dto.UserRegisterResponse;
import com.judge.myojudge.model.dto.UserResponse;
import com.judge.myojudge.model.entity.Submission;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.model.entity.UserRole;
import com.judge.myojudge.model.mapper.UserMapper;
import com.judge.myojudge.repository.SubmissionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public UserResponse toUpdateUserDTO(User user) {
        List<Submission> submissions=submissionRepo.findAllSubmissionByEmailAndStatus(user.getEmail(),"ACCEPTED");
        Set<Long> problemsSolved=new HashSet<>();
        for(Submission submission:submissions){
            problemsSolved.add(submission.getProblem().getId());
        }
        problemsSolved.stream().toList();
        return UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .city(user.getCity())
                .country(user.getCountry())
                .gender(user.getGender())
                .mobile(user.getMobileNumber())
                .state(user.getState())
                .imageUrl(user.getImageUrl())
                .githubUrl(user.getGithubUrl())
                .dateOfBirth(user.getDateOfBirth())
                .linkedinUrl(user.getLinkedinUrl())
                .facebookUrl(user.getFacebookUrl())
                .postalCode(user.getPostalCode())
                .totalPresentCoins(user.getTotalPresentCoins())
                .totalProblemsSolved(user.getTotalProblemsSolved())
                .totalProblemsAttempted(user.getTotalProblemsAttempted())
                .totalProblemsCE(user.getTotalProblemsCE())
                .totalProblemsTLE(user.getTotalProblemsTLE())
                .totalProblemsWA(user.getTotalProblemsWA())
                .totalProblemsRE(user.getTotalProblemsRE())
                .activityStatus(user.getActivityStatus())
                .totalCoinsEarned(user.getTotalCoinsEarned())
                .totalCoinsExpend(user.getTotalCoinsExpend())
                .createdTime(user.getCreatedAt())
                .updateTime(user.getUpdatedAt() == null ? user.getCreatedAt() : user.getUpdatedAt())
                .userSolvedProblems(problemsSolved.stream().toList())
                .build();
    }

    @Override
    public List<UserResponse> toUsersResponse(List<User> ranking) {
        List<UserResponse> userResponses =new ArrayList<>();
        for(User user:ranking){
            UserResponse userResponse = UserResponse.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .country(user.getCountry())
                    .mobile(user.getMobileNumber())
                    .imageUrl(user.getImageUrl()==null?"":user.getImageUrl())
                    .activityStatus(user.getActivityStatus()==null?false:user.getActivityStatus())
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
                    .build();
            userResponses.add(userResponse);
        }
        return userResponses;
    }

    @Override
    public UserResponse toUserUrlCoin(User user) {
        return UserResponse.builder()
                .imageUrl(user.getImageUrl())
                .totalPresentCoins(user.getTotalPresentCoins())
                .build();
    }

    @Override
    public UserResponse toUserResponse(User user) {
        return  UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .country(user.getCountry())
                .mobile(user.getMobileNumber())
                .imageUrl(user.getImageUrl()==null?"":user.getImageUrl())
                .activityStatus(user.getActivityStatus()==null?false:user.getActivityStatus())
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
                .build();
    }

}
