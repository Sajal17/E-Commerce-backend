package com.sa.M_Mart.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_token",
           indexes = {
                @Index(name ="idx_token",columnList="token"),
                @Index(name = "idx_user_id",columnList = "user_id")
           })
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true,length = 512)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    @CreationTimestamp
    @Column(updatable = false)
    private  Instant createAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private AppUser user;

}
