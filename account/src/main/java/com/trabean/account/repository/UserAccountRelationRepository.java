package com.trabean.account.repository;

import com.trabean.account.domain.UserAccountRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRelationRepository extends JpaRepository<UserAccountRelation, Long> {

    @Query("SELECT u FROM UserAccountRelation u WHERE u.userId = :userId AND u.accountId = :accountId")
    Optional<UserAccountRelation> findByUserIdAndAccountId(Long userId, Long accountId);
}