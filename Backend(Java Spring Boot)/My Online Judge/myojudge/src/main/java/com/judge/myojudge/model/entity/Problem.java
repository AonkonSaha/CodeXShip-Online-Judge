package com.judge.myojudge.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name="problems")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Problem {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Column(unique=true, nullable=false, name="handle_name")
    private String handleName;
    private String title;
    @Lob
    @Column(name = "problem_statement")
    private String problemStatement;
    private String difficulty;
    private String type;
    private Long coins;

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TestCase>  testcases;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Submission> submissions=new HashSet<>();


}
