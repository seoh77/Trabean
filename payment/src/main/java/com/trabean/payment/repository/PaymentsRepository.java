package com.trabean.payment.repository;

import com.trabean.payment.entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentsRepository extends JpaRepository<Payments, Long> {
    // 필요한 추가 메소드가 있다면 여기서 정의할 수 있습니다.
}
