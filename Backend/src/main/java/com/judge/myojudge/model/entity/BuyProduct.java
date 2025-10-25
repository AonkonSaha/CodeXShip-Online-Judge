package com.judge.myojudge.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_buy")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuyProduct {
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

}
