package com.judge.myojudge.validation;

import java.time.LocalDate;
import java.util.Set;

public interface UserValidation {
    boolean isEmptyUserName(String name);
    boolean isExitUserByContact(String contact);
    boolean isEmptyUserContact(String contact);
    boolean isValidUserContactLength(String contact);
    boolean isValidUserPasswordLength(String password);
    boolean isValidEmailFormat(String email);
    boolean isValidGender(String gender);
    boolean isExitUserById(Long id);
    boolean isExitUserPassword(String contact,String password);
    boolean isEqualNewPassAndConfirmPass(String newPass, String confirmPass);
    boolean isValidUserAge(LocalDate BirthDate);
    boolean isValidUserContactDigit(String mobileNumber);
    boolean isExitUserEmail(String email);
}
