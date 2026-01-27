package com.judge.myojudge.service.redis;

import com.judge.myojudge.model.dto.UserResponse;
import com.judge.myojudge.model.dto.redis.CacheUserAuth;

public interface UserRedisService {
    void saveCacheUser(UserResponse userResponse);
    UserResponse findCacheUser(String mobileOrEmail);
    void updateCacheUser(UserResponse userResponse);
    void deleteCacheUser(String email);
    void saveCacheUserAuth(CacheUserAuth requestCache);
    CacheUserAuth findCacheUserAuth(String email);
    void updateCacheUserAuth(CacheUserAuth cacheRequest);
    void deleteCacheUserAuth(String email);
    void deleteCacheUserAuthRole(String email, String roleName);
}
