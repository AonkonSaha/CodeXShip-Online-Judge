package com.judge.myojudge.service;

import com.judge.myojudge.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
public interface RankService {
    Page<User> getRanking(String search, Pageable pageable);
    List<User> getTop10();
}
