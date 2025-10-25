package com.judge.myojudge.model.mapper.imp;

import com.judge.myojudge.enums.Role;
import com.judge.myojudge.model.dto.RegisterUserDTO;
import com.judge.myojudge.model.dto.UserDTO;
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
    public User toUser(RegisterUserDTO registerUserDTO) {
        UserRole userRole = new UserRole();
        userRole.setRoleName(Role.NORMAL_USER.name());
        User user= User.builder()
               .username(registerUserDTO.getUsername())
               .email(registerUserDTO.getEmail())
                .gender(registerUserDTO.getGender())
               .mobileNumber(registerUserDTO.getMobile())
               .userRoles(Set.of(userRole))
               .password(passwordEncoder.encode(registerUserDTO.getPassword()))
               .build();
        userRole.setUsers(Set.of(user));
        return user;
    }

    @Override
    public RegisterUserDTO toUserRegisterDTO(User user) {
        return RegisterUserDTO.builder()
                .username(user.getUsername())
                .mobile(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Override
    public UserDTO toUpdateUserDTO(User user) {
        List<Submission> submissions=submissionRepo.findAllSubmissionByContactAndStatus(user.getMobileNumber(),"ACCEPTED");
        Set<Long> problemsSolved=new HashSet<>();
        for(Submission submission:submissions){
            problemsSolved.add(submission.getProblem().getId());
        }
        problemsSolved.stream().toList();
        return UserDTO.builder()
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
    public List<UserDTO> toUserDTO(List<User> ranking) {
        List<UserDTO> userDTOS=new ArrayList<>();
        for(User user:ranking){
            UserDTO userDTO=UserDTO.builder()
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
                    .roles(new HashSet<>(user.getUserRoles().stream().map(UserRole::getRoleName).toList()))
                    .build();
            userDTOS.add(userDTO);
        }
        return userDTOS;
    }

    @Override
    public UserDTO toUserUrlCoin(User user) {
        return UserDTO.builder()
                .imageUrl(user.getImageUrl())
                .totalPresentCoins(user.getTotalPresentCoins())
                .build();
    }

}
