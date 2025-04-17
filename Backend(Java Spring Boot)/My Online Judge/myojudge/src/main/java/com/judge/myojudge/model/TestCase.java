package com.judge.myojudge.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_cases")
@Data @AllArgsConstructor @NoArgsConstructor
@Builder
public class TestCase{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String filePath;
    private String handle;
    private String fileKey;
    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

}





