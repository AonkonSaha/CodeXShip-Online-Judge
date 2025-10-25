package com.judge.myojudge.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "blocked_token")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BlockedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Lob
    @Column(columnDefinition = "TEXT")
    String token;

}
