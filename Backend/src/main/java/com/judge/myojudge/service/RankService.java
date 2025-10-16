package com.judge.myojudge.service;

import com.judge.myojudge.model.entity.User;

import java.util.List;
public interface RankService {
    List<User> getRanking();
    List<User> getTop10();
}
