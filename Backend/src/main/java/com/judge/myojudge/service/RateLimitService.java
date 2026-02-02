package com.judge.myojudge.service;

import io.github.bucket4j.Bucket;

public interface RateLimitService {

    Bucket resolveBucket(String key);
}
