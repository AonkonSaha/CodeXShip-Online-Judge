package com.judge.myojudge.service.imp;

import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.repository.UserRepo;
import com.judge.myojudge.service.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankServiceImp implements RankService {
    private final UserRepo userRepo;
    @Override
    public List<User> getRanking() {
        List<User> userRankingList=userRepo.findAllUserByRank();
        return userRankingList;
    }

    @Override
    public List<User> getTop10() {
        return List.of();
    }
}
