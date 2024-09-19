package com.trabean.travel.repository;

import com.trabean.travel.entity.ForeignTravelAccount;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForeignTravelAccountRepository extends JpaRepository<ForeignTravelAccount, Long> {

    List<ForeignTravelAccount> findByParentAccountId(Long parentAccountId);

}
