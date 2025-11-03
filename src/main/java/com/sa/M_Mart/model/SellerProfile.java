package com.sa.M_Mart.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seller_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerProfile {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     // Link SellerProfile to AppUser (seller is still a user)
     @OneToOne(fetch = FetchType.EAGER)
     @JoinColumn(name = "user_id", nullable = false, unique = true)
     private AppUser user;

     // Company details
     @Column(nullable = false)
     private String companyName="N/A";

     @Column(nullable = false)
     private String businessAddress="";

     // Banking details
     @Column(nullable = false)
     private String bankAccountNumber="";

     @Column(nullable = false)
     private String ifscCode="";

     private String bankName;
     private String branchName;

     @Column(nullable = false)
     private boolean verified = false;
     private boolean active;

     private String verifiedBy;

}
