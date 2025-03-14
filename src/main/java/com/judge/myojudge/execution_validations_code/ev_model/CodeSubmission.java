package com.judge.myojudge.execution_validations_code.ev_model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="code_sub")
public class CodeSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    @Lob
    private String userCode;
    private String language; // Java, Python, C++
    private Long problemId;
    private String status; // "Pending", "Success", "Failed"
    @CreationTimestamp
    private LocalDateTime createdAt;
}

