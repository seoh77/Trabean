package com.trabean.account.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "userAccounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userAccountId")
    private Long userAccountRelationId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole userRole;

    public enum UserRole {
        ADMIN,
        PAYER,
        NONE_PAYER
    }
}
