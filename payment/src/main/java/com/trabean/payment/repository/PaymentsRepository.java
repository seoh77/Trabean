package com.trabean.payment.repository;

import com.trabean.payment.entity.Payments;
import com.trabean.payment.enums.MerchantCategory;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentsRepository extends JpaRepository<Payments, Long> {
    @Query("SELECT p FROM Payments p WHERE p.accountId = :accountId AND p.paymentStatus = 'SUCCESS' AND p.paymentDate BETWEEN :startdate AND :enddate ORDER BY p.paymentDate DESC")
    Page<Payments> findAllByAccountIdDateRange(
            @Param("accountId") Long accountId,
            @Param("startdate") LocalDateTime startdate,
            @Param("enddate") LocalDateTime enddate,
            Pageable pageable
    );

    @Query("SELECT p.merchant.category AS category, SUM(p.krwAmount) AS totalAmount "
            + "FROM Payments p "
            + "WHERE p.accountId = :accountId AND p.paymentStatus = 'SUCCESS' "
            + "AND p.paymentDate BETWEEN :startdate AND :enddate "
            + "GROUP BY p.merchant.category"
    )
    List<CategorySummary> findCategorySummaryByAccountIdAndDateRange(
            @Param("accountId") Long accountId,
            @Param("startdate") LocalDateTime startdate,
            @Param("enddate") LocalDateTime enddate);

    @Query("SELECT p FROM Payments p JOIN p.merchant m "
            + "WHERE p.accountId = :accountId AND m.category = :categoryName "
            + "AND p.paymentStatus = 'SUCCESS' "
            + "AND p.paymentDate BETWEEN :startdate AND :enddate ORDER BY p.paymentDate DESC")
    Page<Payments> findAllByCategoryAndDateRange(
            @Param("accountId") Long accountId,
            @Param("startdate") LocalDateTime startdate,
            @Param("enddate") LocalDateTime enddate,
            @Param("categoryName") MerchantCategory categoryName,
            Pageable pageable);
}
