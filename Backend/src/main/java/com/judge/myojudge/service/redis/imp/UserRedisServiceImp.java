package com.judge.myojudge.service.redis.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.judge.myojudge.keys.RedisKey;
import com.judge.myojudge.model.dto.UserResponse;
import com.judge.myojudge.model.dto.redis.CacheUserAuth;
import com.judge.myojudge.service.redis.UserRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRedisServiceImp implements UserRedisService{
    private final RedisTemplate<String,Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void saveCacheUser(UserResponse userResponse) {
        redisTemplate.opsForValue().set(RedisKey.PER_USER_BASE_KEY+userResponse.getEmail(),userResponse);
    }
    @Override
    public void updateCacheUser(UserResponse userResponse) {
        redisTemplate.opsForValue().set(RedisKey.PER_USER_BASE_KEY+userResponse.getEmail(),userResponse);
    }

    @Override
    public void deleteCacheUser(String email) {
        redisTemplate.delete(RedisKey.PER_USER_BASE_KEY+email);
    }

    @Override
    public UserResponse findCacheUser(String email) {
        return objectMapper.convertValue(redisTemplate
                        .opsForValue()
                        .get(RedisKey.PER_USER_BASE_KEY+email),UserResponse.class);
    }

    @Override
    public void saveCacheUserAuth(CacheUserAuth requestCache) {
        redisTemplate.opsForValue().set(RedisKey.USER_AUTH+requestCache.getEmail(),requestCache);
    }

    @Override
    public CacheUserAuth findCacheUserAuth(String email) {
        return objectMapper.convertValue(redisTemplate.opsForValue().get(RedisKey.USER_AUTH+email),CacheUserAuth.class);
    }

    @Override
    public void updateCacheUserAuth(CacheUserAuth cacheRequest) {
        redisTemplate.opsForValue().set(RedisKey.USER_AUTH+cacheRequest.getEmail(),cacheRequest);

    }

    @Override
    public void deleteCacheUserAuth(String email) {
        redisTemplate.delete(RedisKey.USER_AUTH+email);
    }

    @Override
    public void deleteCacheUserAuthRole(String email, String roleName) {
        CacheUserAuth cacheUserAuth=findCacheUserAuth(email);
        List<String>cachedRoles=cacheUserAuth.getRoleNames();
        for(String cacheRoleName:cachedRoles){
            if(cacheRoleName.equals(roleName)){
                cachedRoles.remove(cacheRoleName);
                break;
            }
        }
        cacheUserAuth.setRoleNames(cachedRoles);
        updateCacheUserAuth(cacheUserAuth);
    }
}
