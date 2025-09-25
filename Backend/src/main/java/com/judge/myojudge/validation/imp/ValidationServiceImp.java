package com.judge.myojudge.validation.imp;

import com.judge.myojudge.exception.*;
import com.judge.myojudge.model.dto.*;
import com.judge.myojudge.model.mapper.DtoMapper;
import com.judge.myojudge.repository.UserRepo;
import com.judge.myojudge.validation.ProblemValidation;
import com.judge.myojudge.validation.UserValidation;
import com.judge.myojudge.validation.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ValidationServiceImp implements ValidationService {

    private final UserValidation userValidation;
    private final DtoMapper dtoMapper;
    private final UserRepo userRepo;
    private final ProblemValidation problemValidation;

    @Override
    public void validateUserDetails(RegisterDTO registerDTO) {
        if(userValidation.isEmptyUserName(registerDTO.getUsername())) {
            throw new InvalidUserArgumentException("Username is empty");
        }
        if(userValidation.isEmptyUserPassword(registerDTO.getPassword())){
            throw new InvalidUserArgumentException("Password is empty");
        }
        if(userValidation.isEmptyUserPassword(registerDTO.getConfirmPassword())){
            throw new InvalidUserArgumentException("Confirm Password is empty");
        }
        if(userValidation.isEmptyUserContact(registerDTO.getMobile())){
            throw new InvalidUserArgumentException("Contact is empty");
        }
        if(userValidation.isEmptyUserEmail(registerDTO.getEmail())) {
            throw new InvalidUserArgumentException("Email is empty");
        }
        if(userValidation.isEmptyUserGender(registerDTO.getGender())) {
            throw new InvalidUserArgumentException("Gender is empty");
        }
        if(userValidation.isExitUserByContact(registerDTO.getMobile())){
            throw new InvalidUserArgumentException("Mobile is already in use");
        }
        if (userValidation.isExitUserEmail(registerDTO.getEmail())){
            throw new InvalidUserArgumentException("Email is already in use");
        }

        if(!userValidation.isValidUserContactLength(registerDTO.getMobile())){
            throw new InvalidUserArgumentException("Mobile Number must be 11 digits!");
        }
        if(!userValidation.isValidUserContactDigit(registerDTO.getMobile())){
            throw new InvalidUserArgumentException("Mobile Number must be contain only digits!");
        }
        if(!userValidation.isValidUserPasswordLength(registerDTO.getPassword())){
            throw new InvalidUserArgumentException("Password must be at least 8 characters long!");
        }
        if(!registerDTO.getEmail().isEmpty() && !userValidation.isValidEmailFormat(registerDTO.getEmail())){
            throw new InvalidUserArgumentException("Email is not valid!");
        }
        if(!userValidation.isValidGender(registerDTO.getGender())){
            throw new InvalidUserArgumentException("Gender must be male,female or other!");
        }


    }

    @Override
    public void validateUserDetails(UpdateUserDTO updateUserDTO) {
        validateUserDetails(dtoMapper.toRegisterDTO(updateUserDTO));
    }

    @Override
    public void validateLoginDetails(LoginDTO loginDTO) {
        if (userValidation.isEmptyUserContact(loginDTO.getMobile())) {
            throw new InvalidLoginArgumentException("Contact is empty");
        }
        if (loginDTO.getPassword() == null || loginDTO.getPassword().isEmpty()) {
            throw new InvalidLoginArgumentException("Password is empty");
        }
        if (!userValidation.isExitUserByContact(loginDTO.getMobile())) {
            throw new InvalidLoginArgumentException("User Not Found with this Contact: " + loginDTO.getMobile());
        }
        if (!userValidation.isExitUserPassword(loginDTO.getMobile(), loginDTO.getPassword())) {
            throw new InvalidLoginArgumentException("Invalid Credentials");
        }


    }

    @Override
    public void validateUserPassword(PasswordDTO passwordDTO) {
        String mobile= SecurityContextHolder.getContext().getAuthentication().getName();
        if(userValidation.isEmptyUserContact(mobile)){
            throw new UserNotFoundException("Contact is empty");
        }
        if (passwordDTO.getOldPassword() == null || passwordDTO.getOldPassword().isEmpty()) {
            throw new InvalidPasswordArgumentException("Old Password is empty");
        }
        if (passwordDTO.getNewPassword() == null || passwordDTO.getNewPassword().isEmpty()) {
            throw new InvalidPasswordArgumentException("New Password is empty");
        }
        if (passwordDTO.getConfirmPassword() == null || passwordDTO.getConfirmPassword().isEmpty()) {
            throw new InvalidPasswordArgumentException("Confirm Password is empty");
        }
        if (!userValidation.isEqualNewPassAndConfirmPass(passwordDTO.getNewPassword(), passwordDTO.getConfirmPassword())) {
            throw new InvalidPasswordArgumentException("New Password and Confirm Password must be same");
        }
        if (!userValidation.isValidUserPasswordLength(passwordDTO.getNewPassword())) {
            throw new InvalidPasswordArgumentException("Password must be at least 8 characters long!");
        }
        if(!userValidation.isExitUserPassword(mobile,passwordDTO.getOldPassword())){
            throw new InvalidPasswordArgumentException("Invalid Old Password");
        }
    }

    @Override
    public void validateProblemDetails(ProblemDTO problemDTO) {
        if(problemValidation.isEmptyProblemTitle(problemDTO.getTitle())){
            throw new InvalidProblemArgumentException("Title is empty");
        }
        if(problemValidation.isEmptyProblemHandle(problemDTO.getHandle())){
            throw new InvalidProblemArgumentException("Handle is empty");
        }
        if(problemValidation.isEmptyProblemType(problemDTO.getType())){
            throw new InvalidProblemArgumentException("Type is empty");
        }
        if(problemValidation.isEmptyProblemDifficulty(problemDTO.getDifficulty())){
            throw new InvalidProblemArgumentException("Difficulty is empty");
        }
        if(problemValidation.isEmptyProblemStatement(problemDTO.getProblemStatement())){
            throw new InvalidProblemArgumentException("Statement is empty");
        }
        if(problemValidation.isEmptyProblemTestcases(problemDTO.getFiles())){
            throw new InvalidProblemArgumentException("Testcases is empty");
        }
        if(problemValidation.isExitProblemHandle(problemDTO.getHandle())){
            throw new InvalidProblemArgumentException("Handle is already exit");
        }
        if(problemValidation.isExitProblemTitle(problemDTO.getTitle())){
            throw new InvalidProblemArgumentException("Title is already exit");
        }
        if(problemValidation.isOverProblemStatementLimit(problemDTO.getProblemStatement())){
            throw new InvalidProblemArgumentException("Statement must be contained 8000 characters");
        }
        if(problemValidation.isOverProblemTestcaseLimit(problemDTO.getFiles())){
            throw new InvalidProblemArgumentException("Testcase File must be contained 5MB");
        }

        if(!problemValidation.isMissMatchTestcase(problemDTO.getFiles()).isEmpty()){
            List<String> missMatch=problemValidation.isMissMatchTestcase(problemDTO.getFiles());
            throw new InvalidTestCaseArgumentException("Testcase MissMatch Number :  "+missMatch);
        }
    }
}
