package com.judge.myojudge.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.judge.myojudge.enums.Role;
import com.judge.myojudge.exception.InvalidPasswordArgumentException;
import com.judge.myojudge.exception.InvalidUserArgumentException;
import com.judge.myojudge.exception.UserNotFoundException;
import com.judge.myojudge.jwt.JwtUtil;
import com.judge.myojudge.model.dto.LoginRequest;
import com.judge.myojudge.model.dto.PasswordRequest;
import com.judge.myojudge.model.dto.UserResponse;
import com.judge.myojudge.model.dto.UserUpdateRequest;
import com.judge.myojudge.model.dto.redis.CacheUserAuth;
import com.judge.myojudge.model.entity.BlockedToken;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.model.entity.UserRole;
import com.judge.myojudge.model.mapper.UserMapper;
import com.judge.myojudge.repository.BlockedTokenRepo;
import com.judge.myojudge.repository.UserRepo;
import com.judge.myojudge.service.AuthService;
import com.judge.myojudge.service.CloudinaryService;
import com.judge.myojudge.service.redis.RankRedisService;
import com.judge.myojudge.service.redis.UserRedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {
    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final BlockedTokenRepo blockedTokenRepo;
    private final CloudinaryService cloudinaryService;
    private final RedisTemplate<String,Object> redisTemplate;
    private final UserMapper userMapper;
    private final RankRedisService rankRedisService;
    private final ObjectMapper objectMapper;
    private final UserRedisService userRedisService;

    @Override
    public String login(LoginRequest loginRequest) {
        User user = userRepository.findByMobileOrEmail(loginRequest.getMobileOrEmail())
                .orElseThrow(() -> new BadCredentialsException("User xxx not found"));
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
           throw new BadCredentialsException("Incorrect Password");
        }
        user.setActivityStatus(true);
        if(user.getLastLogin()==null || user.getLastLogin().toLocalDate().isBefore(LocalDate.now())){
            user.setTotalPresentCoins(user.getTotalPresentCoins()==null?5:user.getTotalPresentCoins()+ 5);
            user.setTotalCoinsEarned(user.getTotalCoinsEarned()==null?5:user.getTotalCoinsEarned()+5);
            user.setIsAdditionDailyCoin(true);
            user.setNumOfDaysLogin(user.getNumOfDaysLogin()==null?1:user.getNumOfDaysLogin()+1);
        }
        user.setLastLogin(LocalDateTime.now());
        String token = jwtUtil.generateToken(user);
        user.setIsAdditionDailyCoin(false);
        userRepository.save(user);
        userRedisService.updateCacheUser(userMapper.toUserResponse(user));
        return token;
    }

    @Override
    public void logout(String mobileOrEmail,String token)
    {
        User user = userRepository.findByMobileOrEmail(mobileOrEmail).orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setActivityStatus(false);
        blockedTokenRepo.save(BlockedToken.builder().token(token).build());
        userRepository.save(user);
        userRedisService.updateCacheUser(userMapper.toUserResponse(user));
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        if(user.getGender().equalsIgnoreCase("Male")){
            user.setImageUrl("https://res.cloudinary.com/dagkiubxf/image/upload/v1760908494/Default_Men_ujdzoj.png");
        }else{
            user.setImageUrl("https://res.cloudinary.com/dagkiubxf/image/upload/v1760928719/default_Female_bsbwrk.png");
        }
        user.setIsGoogleUser(false);
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional
    public User updateUserDetails(String mobileOrEmail, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findByMobileOrEmail(mobileOrEmail).orElseThrow(() -> new UserNotFoundException("User not found"));
        if(user.getMobileNumber()!=null &&
                !user.getMobileNumber().isEmpty() &&
                !user.getMobileNumber().equals(userUpdateRequest.getMobile())
        ){
            Optional<User> userByMobileNumber=userRepository.findByMobileNumber(userUpdateRequest.getMobile());
            if(userByMobileNumber.isPresent()){
                throw new InvalidUserArgumentException("Mobile number already exists");
            }
        }
        if(user.getEmail()!=null &&
                !user.getEmail().isEmpty() &&
                !user.getEmail().equals(userUpdateRequest.getEmail())){
            if(userRepository.existsByEmail(mobileOrEmail)){
                throw new InvalidUserArgumentException("Mobile number already exists");
            }
        }
        resetUserInfo(user, userUpdateRequest);
        userRepository.save(user);
        userRedisService.updateCacheUser(userMapper.toUserResponse(user));
        return user;
    }

    @Override
    public void updateUserPassword(String mobileOrEmail, PasswordRequest passwordRequest) {
        User user = userRepository.findByMobileOrEmail(mobileOrEmail).orElseThrow(() -> new UserNotFoundException("User not found"));
        if(!passwordRequest.getNewPassword().equals(passwordRequest.getConfirmPassword())){
            throw new InvalidPasswordArgumentException("Passwords do not match");
        }
        if((user.getIsGoogleUser() && user.getIsGoogleUserSetPassword())
                && user.getPassword()!=null && !user.getPassword().isEmpty()
                && !passwordEncoder.matches(passwordRequest.getOldPassword(), user.getPassword()
        )){
            throw new BadCredentialsException("Password incorrect");
        }
        user.setIsGoogleUserSetPassword(true);
        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        userRepository.save(user);

        List<String> roleNames=new ArrayList<>();
        for(UserRole userRole:user.getUserRoles()){
            roleNames.add(userRole.getRoleName());
        }
        CacheUserAuth cacheRequest=CacheUserAuth.builder()
                .roleNames(roleNames)
                .password(user.getPassword())
                .email(user.getEmail())
                .build();
        userRedisService.updateCacheUserAuth(cacheRequest);
    }

    @Override
    public User getUserByMobileOrEmail(String mobileOrEmail) {
       return userRepository.findByMobileOrEmail(mobileOrEmail).orElseThrow(()->new UserNotFoundException("User Not Found"));
    }

    @Override
    public Optional<User> fetchUserByMobileNumber(String mobile) {
        return userRepository.findByMobileNumber(mobile);
    }

    @Override
    public User getUserById(Long userId) {
     return userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public String updateProfileImage(MultipartFile file) throws Exception {
        String mobileOrEmail= SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByMobileOrEmail(mobileOrEmail).orElseThrow(()->new UserNotFoundException("User Not Found"));
        if(user.getImageFileKey()!=null){
            cloudinaryService.deleteCloudinaryFile(user.getImageFileKey(),"image");
        }
        Map uploadedImage = cloudinaryService.uploadImage(file);
        user.setImageUrl(uploadedImage.get("secure_url").toString());
        user.setImageFileKey(uploadedImage.get("public_id").toString());
        userRepository.save(user);
        userRedisService.updateCacheUser(userMapper.toUserResponse(user));//needs to be change
        return uploadedImage.get("secure_url").toString();
    }

    @Override
    public Page<User> getUsers(String search, Pageable pageable) {
        return userRepository.findAllUserByCreatedTime(search,pageable);
    }

    @Override
    @Transactional
    public void deleteUser(String email) {
        User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("User not found"));
        user.getProblems().forEach((problem)-> problem.setUser(null));
        user.getSubmissions().forEach((submission -> submission.setUser(null)));
        userRepository.delete(user);
        userRedisService.deleteCacheUser(user.getEmail());
        userRedisService.deleteCacheUserAuth(user.getEmail());

    }

    @Override
    @Transactional
    public void updateUserDetailsByAdmin(UserUpdateRequest userUpdateRequest) {
        User user = null;
        if(userUpdateRequest.getEmail()!=null && !userUpdateRequest.getEmail().isEmpty()){
            user = userRepository.findByEmail(userUpdateRequest.getEmail()).orElseThrow(()->new UserNotFoundException("User Not Found"));
        }else{
            user = userRepository.findByMobileNumber(userUpdateRequest.getMobile()).orElseThrow(() -> new UserNotFoundException("User Not Found"));
        }
        resetUserInfo(user, userUpdateRequest);
        userRepository.save(user);
        userRedisService.updateCacheUser(userMapper.toUserResponse(user));
    }

    @Override
    @Transactional
    public String loginByGoogle(String email, String name, String picture) {
        User user = userRepository.findByEmail(email).orElse(null);
        boolean isNewUser=false;
        UserRole role = null;
        if(user==null) {
            user = new User();
            user.setEmail(email);
            user.setUsername(name);
            user.setImageUrl(picture);
            user.setIsGoogleUser(true);
            user.setIsGoogleUserSetPassword(false);
            user.setPassword(UUID.randomUUID().toString());
            role = new UserRole();
            role.setRoleName(Role.NORMAL_USER.name());
            role.setUsers(Set.of(user));
            user.setUserRoles(Set.of(role));
            isNewUser=true;
        }
        user.setActivityStatus(true);
        if(user.getLastLogin()==null || user.getLastLogin().toLocalDate().isBefore(LocalDate.now())) {
            user.setTotalPresentCoins(user.getTotalPresentCoins()==null?5:user.getTotalPresentCoins()+ 5);
            user.setTotalCoinsEarned(user.getTotalCoinsEarned()==null?5:user.getTotalCoinsEarned()+5);
            user.setIsAdditionDailyCoin(true);
            user.setNumOfDaysLogin(user.getNumOfDaysLogin()==null?1:user.getNumOfDaysLogin()+1);
        }
        user.setLastLogin(LocalDateTime.now());
        String token = jwtUtil.generateToken(user);
        user.setIsAdditionDailyCoin(false);
        userRepository.save(user);
        userRedisService.updateCacheUser(userMapper.toUserResponse(user));
        return token;
    }

    @Override
    public User fetchUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("User not found"));
    }

    @Override
    public UserResponse fetchUserByProblemSolvedHistory(String mobileOrEmail) {
        return null;
    }

    @Override
    public boolean isExitsUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(value = Transactional.TxType.REQUIRED)
    protected void resetUserInfo(User user, UserUpdateRequest userUpdateRequest) {
        user.setUsername(userUpdateRequest.getUsername()==null?user.getUsername(): userUpdateRequest.getUsername());
        user.setMobileNumber(userUpdateRequest.getMobile()==null?user.getMobileNumber(): userUpdateRequest.getMobile());
        user.setEmail(userUpdateRequest.getEmail()==null?user.getEmail(): userUpdateRequest.getEmail() );
        user.setCountry(userUpdateRequest.getCountry()==null?user.getCountry(): userUpdateRequest.getCountry() );
        user.setState(userUpdateRequest.getState()==null?user.getState(): userUpdateRequest.getState());
        user.setGithubUrl(userUpdateRequest.getGithubUrl()==null?user.getGithubUrl(): userUpdateRequest.getGithubUrl());
        user.setLinkedinUrl(userUpdateRequest.getLinkedinUrl()==null?user.getLinkedinUrl(): userUpdateRequest.getLinkedinUrl());
        user.setFacebookUrl(userUpdateRequest.getFacebookUrl()==null?user.getFacebookUrl(): userUpdateRequest.getFacebookUrl());
        user.setTotalPresentCoins(userUpdateRequest.getTotalPresentCoins()==null?user.getTotalPresentCoins(): userUpdateRequest.getTotalPresentCoins());
        user.setDateOfBirth(userUpdateRequest.getBirthday()==null?user.getDateOfBirth(): userUpdateRequest.getBirthday());
        user.setTotalProblemsSolved(userUpdateRequest.getTotalProblemsSolved()==null?user.getTotalProblemsSolved(): userUpdateRequest.getTotalProblemsSolved());
        user.setTotalProblemsWA(userUpdateRequest.getTotalProblemsAttempted()==null?user.getTotalProblemsWA(): userUpdateRequest.getTotalProblemsAttempted());
        user.setTotalProblemsAttempted(userUpdateRequest.getTotalProblemsAttempted()==null?user.getTotalProblemsAttempted(): userUpdateRequest.getTotalProblemsAttempted());
        user.setActivityStatus(userUpdateRequest.isActivityStatus());
        user.setTotalProblemsCE(userUpdateRequest.getTotalProblemsCE()==null?user.getTotalProblemsCE(): userUpdateRequest.getTotalProblemsCE());
        user.setTotalProblemsRE(userUpdateRequest.getTotalProblemsRE()==null?user.getTotalProblemsRE(): userUpdateRequest.getTotalProblemsRE());
        user.setTotalProblemsTLE(userUpdateRequest.getTotalProblemsTLE()==null?user.getTotalProblemsTLE(): userUpdateRequest.getTotalProblemsTLE());
        user.setCity(userUpdateRequest.getCity()==null?user.getCity(): userUpdateRequest.getCity());
        user.setPostalCode(userUpdateRequest.getPostalCode()==null?user.getPostalCode(): userUpdateRequest.getPostalCode());
        user.setGender(userUpdateRequest.getGender()==null?user.getGender(): userUpdateRequest.getGender());
    }
}
