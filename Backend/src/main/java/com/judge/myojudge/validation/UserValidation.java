package com.judge.myojudge.validation;

import java.time.LocalDate;
import java.util.Set;

public interface UserValidation {
    boolean isEmptyUserName(String name);
    boolean isEmptyUserContact(String contact);
    boolean isEmptyUserPassword(String password);
    boolean isEmptyUserEmail(String email);
    boolean isEmptyUserGender(String gender);
    boolean isValidUserContactLength(String contact);
    boolean isValidUserPasswordLength(String password);
    boolean isValidEmailFormat(String email);
    boolean isValidGender(String gender);
    boolean isValidUserAge(LocalDate BirthDate);
    boolean isValidUserContactDigit(String mobileNumber);
    boolean isExitUserByContact(String contact);
    boolean isExitUserById(Long id);
    boolean isExitUserPassword(String contact,String password);
    boolean isEqualNewPassAndConfirmPass(String newPass, String confirmPass);

    boolean isExitUserEmail(String email);
}
