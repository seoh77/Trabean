package com.trabean.payment.repository;

import com.trabean.payment.entity.Payments;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentsRepository extends JpaRepository<Payments, Long> {
    @Query("SELECT p FROM Payments p WHERE p.accountId = :accountId AND p.paymentStatus = 'SUCCESS' AND DATE(p.paymentDate) BETWEEN :startdate AND :enddate")
    Page<Payments> findAllByAccountIdDateRange(
            @Param("accountId") Long accountId,
            @Param("startdate") LocalDate startdate,
            @Param("enddate") LocalDate enddate,
            Pageable pageable
    );

//    @Query("SELECT p FROM Payments p WHERE p.accountId = :accountId AND ")
}
