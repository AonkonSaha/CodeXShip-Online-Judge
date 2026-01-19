package com.judge.myojudge.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name="submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @Lob
    @Column(columnDefinition = "TEXT")
    private String userCode;
    private String language;
    private String handle;
    private String status;
    private float time;
    private Long memory;
    private int totalTestcases;
    private int passedTestcases;
    private Long coinsEarned;

    @Column(updatable = false,name ="creation_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "submission", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<TestCaseResult> testCaseResults;

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id")
    private User user;


}
