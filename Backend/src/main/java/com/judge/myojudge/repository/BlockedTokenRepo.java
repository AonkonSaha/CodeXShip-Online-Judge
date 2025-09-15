package com.judge.myojudge.repository;

import com.judge.myojudge.model.entity.BlockedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedTokenRepo extends JpaRepository<BlockedToken, Long> {
}
