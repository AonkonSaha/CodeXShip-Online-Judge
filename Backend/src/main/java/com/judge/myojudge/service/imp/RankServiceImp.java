package com.judge.myojudge.service.imp;

import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.repository.UserRepo;
import com.judge.myojudge.service.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankServiceImp implements RankService {
    private final UserRepo userRepo;

    @Override
    public Page<User> getRanking(String search, Pageable pageable) {
        return userRepo.findAllUserByRank(search,pageable);
    }

    @Override
    public List<User> getTop10() {
        return List.of();
    }
}
