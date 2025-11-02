package com.judge.myojudge.service.imp;

import com.judge.myojudge.exception.InvalidPasswordArgumentException;
import com.judge.myojudge.exception.InvalidUserArgumentException;
import com.judge.myojudge.exception.UserNotFoundException;
import com.judge.myojudge.jwt.JwtUtil;
import com.judge.myojudge.model.dto.LoginDTO;
import com.judge.myojudge.model.dto.PasswordDTO;
import com.judge.myojudge.model.dto.UpdateUserDTO;
import com.judge.myojudge.model.entity.BlockedToken;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.repository.BlockedTokenRepo;
import com.judge.myojudge.repository.UserRepo;
import com.judge.myojudge.service.AuthService;
import com.judge.myojudge.service.CloudinaryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {
    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final BlockedTokenRepo blockedTokenRepo;
    private final CloudinaryService cloudinaryService;

    @Override
    public User register(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public String login(LoginDTO loginDTO) {
        User user = userRepository.findByMobileNumber(loginDTO.getMobile()).get();
        if(!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
           throw new BadCredentialsException("Incorrect Password");
        }
        user.setActivityStatus(true);
        if(Duration.between(
                (user.getLastLogin()==null? LocalDateTime.now():user.getLastLogin()),LocalDateTime.now()).toHours() >= 24L){
            user.setTotalPresentCoins(user.getTotalPresentCoins()==null?5:user.getTotalPresentCoins()+ 5);
            user.setTotalCoinsEarned(user.getTotalCoinsEarned()==null?5:user.getTotalCoinsEarned()+5);
            user.setIsAdditionDailyCoin(true);
            user.setNumOfDaysLogin(user.getNumOfDaysLogin()==null?1:user.getNumOfDaysLogin()+1);
        }
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        return jwtUtil.generateToken(user,user.getMobileNumber(),user.getActivityStatus());
    }



    @Override
    public void logout(String mobileNumber,String token)
    {
        Optional<User> user=userRepository.findByMobileNumber(mobileNumber);
        if(user.isEmpty()){
          throw new UserNotFoundException("User not found");
        }
        user.get().setActivityStatus(false);
        blockedTokenRepo.save(BlockedToken.builder().token(token).build());
        userRepository.save(user.get());
    }

    @Override
    public User saveUser(User user) {
        if(user.getGender().equalsIgnoreCase("Male")){
            user.setImageUrl("https://res.cloudinary.com/dagkiubxf/image/upload/v1760908494/Default_Men_ujdzoj.png");
        }else{
            user.setImageUrl("https://res.cloudinary.com/dagkiubxf/image/upload/v1760928719/default_Female_bsbwrk.png");
        }
        return userRepository.save(user);
    }

    @Override
    public User updateUserDetails(String mobile,UpdateUserDTO updateUserDTO) {
        User user = userRepository.findByMobileNumber(mobile).orElseThrow(()->new UserNotFoundException("User not found"));
        user.setUsername(updateUserDTO.getUsername());
        if(!mobile.equals(updateUserDTO.getMobile())){
            Optional<User> userByMobileNumber=userRepository.findByMobileNumber(updateUserDTO.getMobile());
            if(userByMobileNumber.isPresent()){
                throw new InvalidUserArgumentException("Mobile number already exists");
            }
        }
        if(!user.getEmail().equalsIgnoreCase(updateUserDTO.getEmail())){
            Optional<User> userByEmail=userRepository.findByEmail(updateUserDTO.getEmail());
            if(userByEmail.isPresent()){
                throw new InvalidUserArgumentException("Email already exists");
            }
        }
        user.setMobileNumber(updateUserDTO.getMobile());
        user.setEmail(updateUserDTO.getEmail());
        user.setCountry(updateUserDTO.getCountry());
        user.setState(updateUserDTO.getState());
        user.setGithubUrl(updateUserDTO.getGithubUrl());
        user.setLinkedinUrl(updateUserDTO.getLinkedinUrl());
        user.setFacebookUrl(updateUserDTO.getFacebookUrl());
        user.setDateOfBirth(updateUserDTO.getBirthday());
        user.setCity(updateUserDTO.getCity());
        user.setPostalCode(updateUserDTO.getPostalCode());
        user.setGender(updateUserDTO.getGender());
        return userRepository.save(user);

    }

    @Override
    public void updateUserPassword(String mobile,PasswordDTO passwordDTO) {
        User user = userRepository.findByMobileNumber(mobile).orElseThrow(()->new UserNotFoundException("User not found"));
        if(!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())){
            throw new InvalidPasswordArgumentException("Passwords do not match");
        }
        if(!passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())){
            throw new BadCredentialsException("Password incorrect");
        }
        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public User fetchUserDetails(String mobile) {
        return userRepository.findByMobileNumber(mobile).orElseThrow(()->new UserNotFoundException("User not found"));
    }

    @Override
    public Optional<User> fetchUserByMobileNumber(String mobile) {
        return userRepository.findByMobileNumber(mobile);
    }

    @Override
    public User getUserCoinWithImgUrl(String contact) {
        return userRepository.findByMobileNumber(contact).orElseThrow(()->new UserNotFoundException("User not found"));
    }

    @Override
    public User fetchUserDetailsByUsername(String username,Long userId) {
     return userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User not found"));
    }

    @Override
    public String updateProfileImage(MultipartFile file) throws Exception {
        String contact= SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByMobileNumber(contact).orElseThrow(()->new UserNotFoundException("User not found"));
        if(user.getImageFileKey()!=null ){
            cloudinaryService.deleteCloudinaryFile(user.getImageFileKey(),"image");
        }
        Map uploadedImage = cloudinaryService.uploadImage(file);
        user.setImageUrl(uploadedImage.get("secure_url").toString());
        user.setImageFileKey(uploadedImage.get("public_id").toString());
        userRepository.save(user);
        return uploadedImage.get("secure_url").toString();
    }

    @Override
    public Page<User> getUsers(String search, Pageable pageable) {
        return userRepository.findAllUserByCreatedTime(search,pageable);
    }

    @Override
    @Transactional
    public void deleteUser(String mobileNumber) {
        User user=userRepository.findByMobileNumber(mobileNumber).orElseThrow(()->new UserNotFoundException("User not found"));
        user.getProblems().forEach((problem)-> problem.setUser(null));
        user.getSubmissions().forEach((submission -> submission.setUser(null)));
        userRepository.delete(user);
    }
}
