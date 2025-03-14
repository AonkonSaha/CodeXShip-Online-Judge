package com.judge.myojudge.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class UserRegister {
    String username;
    String email;
    String password;
}
