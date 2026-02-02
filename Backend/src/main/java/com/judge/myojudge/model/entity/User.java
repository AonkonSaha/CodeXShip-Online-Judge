package com.judge.myojudge.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "musers")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(name = "user_name", nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, name="mobile_number")
    private String mobileNumber;
    @Column(nullable = true)
    private String password;
    @Column(name= "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "image_url")
    private String imageUrl;
    private String imageFileKey;
    private Boolean isGoogleUser=false;
    private Boolean isGoogleUserSetPassword;
    private String gender;
    private String country;
    private String state;
    private String city;
    @Column(name = "postal_code")
    private String postalCode;

    @Column(name="fb_url")
    private String facebookUrl;
    @Column(name= "linkedin_url")
    private String linkedinUrl;
    @Column(name = "github_url")
    private String githubUrl;


    @Column(name = "activity_status")
    private Boolean activityStatus;
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    @Column(name = "num_of_days_login")
    private Long numOfDaysLogin=0L;
    @Column(name = "is_addition_daily_coin")
    private Boolean isAdditionDailyCoin;
    @Column(name = "total_coins_earned")
    private Long totalCoinsEarned = 0L;
    @Column(name= "total_coins_expend")
    private Long totalCoinsExpend = 0L;
    @Column(name= "total_present_coins")
    private Long totalPresentCoins = 0L;
    @Column(name= "total_problems_solved")
    private Long totalProblemsSolved = 0L;
    @Column(name= "total_problems_attempted")
    private Long totalProblemsAttempted = 0L;
    @Column(name= "total_problems_failed")
    private Long totalProblemsFailed = 0L;
    @Column(name= "total_problems_pending")
    private Long totalProblemsPending = 0L;
    @Column(name= "total_problems_tle")
    private Long totalProblemsTLE = 0L;
    @Column(name= "total_problems_re")
    private Long totalProblemsRE = 0L;
    @Column(name= "total_problems_wa")
    private Long totalProblemsWA = 0L;
    @Column(name= "total_problems_ce")
    private Long totalProblemsCE = 0L;

    @CreationTimestamp
    @Column(updatable = false,name ="creation_at", nullable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<UserRole> userRoles =new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private Set<Problem> problems=new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private Set<Submission> submissions=new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private Set<Order> order = new HashSet<>();

}
