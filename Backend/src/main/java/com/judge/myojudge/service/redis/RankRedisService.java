package com.judge.myojudge.service.redis;

import com.judge.myojudge.model.dto.UserResponse;
import com.judge.myojudge.model.dto.redis.RankCacheResponse;

import java.util.List;

public interface RankRedisService {
    RankCacheResponse getCachedRanking(int page, int size, String search);
    void cacheRankingPage(int page, int size, String search,Long totalElements, List<UserResponse> userResponses);
    void invalidateAllRankingPages();
    void invalidateAffectedPages(Long userId, int pageSize, Long totalUsers);

    void invalidateLastPageCache(Long id, int pageSize);
}
