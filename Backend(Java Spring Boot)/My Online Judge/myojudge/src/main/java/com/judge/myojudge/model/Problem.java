package com.judge.myojudge.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="problem")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Problem {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String handle;
    private String title;
    @Lob
    private String problemStatement;
    private String difficulty;
    private String type;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TestCase>  testcases;




}
