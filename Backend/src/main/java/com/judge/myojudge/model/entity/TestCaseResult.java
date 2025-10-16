package com.judge.myojudge.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "test_case_results")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestCaseResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    public String status;
    public String stdout;
    public String expectedOutput;
    public String stderr;
    public String compileOutput;
    public String message;
    public String time;
    public String memory;
    private boolean passed;

    @ManyToOne
    @JoinColumn(name = "submission_id")
    private Submission submission;

}
