package com.judge.myojudge.service.redis.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.judge.myojudge.keys.RedisKey;
import com.judge.myojudge.model.dto.UserResponse;
import com.judge.myojudge.model.dto.redis.RankCacheResponse;
import com.judge.myojudge.model.mapper.UserMapper;
import com.judge.myojudge.repository.UserRepo;
import com.judge.myojudge.service.redis.RankRedisService;
import com.judge.myojudge.service.redis.UserRedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RankRedisServiceImp implements RankRedisService {
    private final RedisTemplate<String,Object> redisTemplate;
    private final UserRepo userRepository;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;
    private final UserRedisService userRedisService;
    @Override
    @Transactional
    public RankCacheResponse getCachedRanking(int page, int size, String search) {
        String key = buildPageKey(page, size, search);
        Object cacheObject = redisTemplate.opsForValue().get(key);
        if(cacheObject == null ){
            return null;
        }
        RankCacheResponse cacheResult = objectMapper.convertValue(cacheObject, RankCacheResponse.class);
        if(cacheResult==null || cacheResult.getTotalElements()==null || cacheResult.getUserIds()==null){
            return null;
        }

        List<Long> userIds = cacheResult.getUserIds();

        Long totalElements = cacheResult.getTotalElements();

        List<UserResponse> users = new ArrayList<>();
        for (Long id : userIds) {
            Object cacheUserObject = redisTemplate.opsForValue().get(RedisKey.PER_USER_BASE_KEY + id);
            UserResponse user= objectMapper.convertValue(cacheUserObject,UserResponse.class);
//            if(user==null ){
//                System.out.println("It is Critical Section for mine.....");
//                Long totalCacheUsers=objectMapper.convertValue(redisTemplate.opsForValue().get(RedisKey.CACHED_TOTAL_USERS),Long.class);
//                if(totalCacheUsers==null)totalCacheUsers=1L;
//                else totalCacheUsers++;
//                User userEntity= userRepository.findById(id).orElseThrow(()-> new UserNotFoundException("User Not Found By "+id+" ID"));
//                user=userMapper.toUserResponse(userEntity);
//                redisTemplate.opsForValue().set(RedisKey.PER_USER_BASE_KEY + id,user);
//                redisTemplate.opsForValue()
//                        .set(RedisKey.CACHED_TOTAL_USERS,totalCacheUsers);
//            }
            users.add(user);
        }
        cacheResult.setUserResponses(users);
        return cacheResult ;
    }

    @Override
    public void cacheRankingPage(
            int page,
            int size,
            String search,
            Long totalElements,
            List<UserResponse> userResponses
    ) {
        String key = buildPageKey(page, size, search);
        List<Long> ids = new ArrayList<>();
        Long totalCacheUsers=objectMapper.convertValue(redisTemplate.opsForValue().get(RedisKey.CACHED_TOTAL_USERS),Long.class);
        Long totalRankCacheUsers=objectMapper.convertValue(redisTemplate.opsForValue().get(RedisKey.CACHED_TOTAL_PAGE_RANK_USERS),Long.class);
        if(totalCacheUsers==null)totalCacheUsers=0L;
        if(totalRankCacheUsers==null)totalRankCacheUsers=0L;
        for (UserResponse u : userResponses) {
                if(redisTemplate.opsForValue().get(RedisKey.PER_USER_BASE_KEY+u.getUserId())==null){
                    redisTemplate.opsForValue().set(RedisKey.PER_USER_BASE_KEY+u.getUserId(),u);
                    totalCacheUsers++;
                }
                totalRankCacheUsers++;
                redisTemplate.opsForValue().set(RedisKey.CACHED_TOTAL_PAGE_RANK_USERS,totalRankCacheUsers);
            ids.add(u.getUserId());
        }
        RankCacheResponse rankCacheResponse = RankCacheResponse.builder()
                .userIds(ids)
                .totalElements(totalElements)
                .build();
        redisTemplate.opsForValue().set(key, rankCacheResponse);
    }

    @Override
    public void invalidateAllRankingPages() {
        Set<String> keys = redisTemplate.keys("ranking:page:*");
        if (keys != null) keys.forEach(redisTemplate::delete);
    }

    /**
     * Invalidate only pages that include this user
     * @param userId user whose rank changed
     * @param pageSize number of users per page
     * @param totalUsers total users in ranking
     */
    @Override
    public void invalidateAffectedPages(Long userId, int pageSize, Long totalUsers) {
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);
        for (int page = 0; page < totalPages; page++) {
            String keyPattern = RedisKey.RANK_PAGE_BASE_KEY + page + ":*"; // all searches for this page
            Set<String> keys = redisTemplate.keys(keyPattern);
            for (String key : keys) {
                Object cacheObject= redisTemplate.opsForValue().get(key);
                RankCacheResponse cacheResponse=objectMapper.convertValue(cacheObject,RankCacheResponse.class);
                List<Long> ids = cacheResponse.getUserIds();
                if (ids != null && ids.contains(userId)) {
                    redisTemplate.delete(key);
                }
            }
        }
    }

    @Override
    public void invalidateLastPageCache(Long id, int pageSize) {
        Long totalCacheUsers=objectMapper.convertValue(redisTemplate.opsForValue().get(RedisKey.BASE_USER_KEY+":total"),Long.class);
        if (totalCacheUsers == null || totalCacheUsers <= 0) {
            return;
        }
        long lastPage =  (totalCacheUsers-1) / pageSize;
        String keyPattern = RedisKey.RANK_PAGE_BASE_KEY + lastPage + ":*";
        Set<String> keys = redisTemplate.keys(keyPattern);
        if (keys.isEmpty()) {
            return;
        }
        for (String key : keys) {
            RankCacheResponse rankCacheResponse=objectMapper.convertValue(redisTemplate.opsForValue().get(key),RankCacheResponse.class);
            decreaseCachedRankUsersNumber((long) rankCacheResponse.getUserIds().size());
            redisTemplate.delete(key);
        }

    }

    private void decreaseCachedRankUsersNumber(long size) {
        Long totalCachedRankUser=objectMapper.convertValue(redisTemplate.opsForValue().get(RedisKey.CACHED_TOTAL_PAGE_RANK_USERS),Long.class);
        if(totalCachedRankUser==null)return;
        redisTemplate.opsForValue().set(RedisKey.CACHED_TOTAL_PAGE_RANK_USERS,totalCachedRankUser-size);
    }


    private String buildPageKey(int page, int size, String search) {
        return RedisKey.RANK_PAGE_BASE_KEY + page + ":" + size + ":" + (search == null ? "" : search.toLowerCase());
    }
}
