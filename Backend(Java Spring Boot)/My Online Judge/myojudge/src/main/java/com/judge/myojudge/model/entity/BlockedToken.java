package com.judge.myojudge.model.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "blocked_token")
@NoArgsConstructor
public class BlockedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String token;
    public BlockedToken(String token) {
        this.token = token;
    }
}
