package com.judge.myojudge.model.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CacheUserAuth {
    private String email;
    private List<String> roleNames;
    private String password;
}
