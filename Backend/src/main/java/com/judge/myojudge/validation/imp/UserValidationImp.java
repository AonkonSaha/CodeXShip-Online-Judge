package com.judge.myojudge.validation.imp;

import com.judge.myojudge.enums.Gender;
import com.judge.myojudge.exception.UserNotFoundException;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.repository.UserRepo;
import com.judge.myojudge.validation.UserValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UserValidationImp implements UserValidation {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean isEmptyUserName(String name) {
        return name==null || name.isEmpty();
    }

    @Override
    public boolean isExitUserByContact(String contact) {
        return userRepo.existsByMobileNumber(contact);
    }

    @Override
    public boolean isEmptyUserContact(String contact) {
        return contact==null || contact.isEmpty();
    }

    @Override
    public boolean isEmptyUserPassword(String password) {
        return password==null || password.isEmpty();
    }

    @Override
    public boolean isEmptyUserEmail(String email) {
        return email==null || email.isEmpty();
    }

    @Override
    public boolean isEmptyUserGender(String gender) {
        return gender==null || gender.isEmpty();
    }

    @Override
    public boolean isValidUserContactLength(String contact) {
        return contact.length() == 11;
    }

    @Override
    public boolean isValidUserPasswordLength(String password) {
        return password.length() >= 8;
    }

    @Override
    public boolean isValidEmailFormat(String email) {
        final String EMAIL_REGEX = "^(?!.*\\.{2})([A-Za-z0-9._%+-]+)@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$";
        final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
        return EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public boolean isValidGender(String gender) {
        return Gender.MALE.toString().equalsIgnoreCase(gender) || Gender.FEMALE.toString().equalsIgnoreCase(gender) || Gender.OTHER.toString().equalsIgnoreCase(gender);
    }

    @Override
    public boolean isExitUserById(Long id) {
        return false;
    }

    @Override
    public boolean isExitUserPassword(String contact, String password) {
        Optional<User> mUser=userRepo.findByMobileNumber(contact);
        if(mUser.isEmpty()){
            throw new UserNotFoundException("User Not Found with this Contact: "+contact);
        }
        return passwordEncoder.matches(password,mUser.get().getPassword());
    }

    @Override
    public boolean isEqualNewPassAndConfirmPass(String newPass, String confirmPass) {
        return newPass.equals(confirmPass) ;
    }

    @Override
    public boolean isValidUserAge(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        return  age>=6 && age<=120;
    }

    @Override
    public boolean isValidUserContactDigit(String mobileNumber) {
        for(int i=0;i<mobileNumber.length();i++){
            if(mobileNumber.charAt(i) >= '0' && mobileNumber.charAt(i) <= '9'){
                continue;
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean isExitUserEmail(String email) {
        return userRepo.existsByEmail(email);
    }
}
