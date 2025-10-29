package com.judge.myojudge.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_buy")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;


}
