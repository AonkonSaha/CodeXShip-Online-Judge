package com.judge.myojudge.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name="problems")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Problem {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Column(unique=true, nullable=false, name="handle_name")
    private String handleName;
    private String title;
//    @Lob
    @Column(name = "problem_statement",columnDefinition = "TEXT")
    private String problemStatement;
    @Column(name = "explanation",columnDefinition = "TEXT")
    private String explanation;
    private String difficulty;
    private String type;
    private Long coins;
    @Column(nullable = true)
    private Double timeLimit;
    @Column(nullable = true)
    private Double memoryLimit;

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TestCase>  testcases;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Submission> submissions=new HashSet<>();

}
