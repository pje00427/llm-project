package com.sparta.msa_project_part_3.domain.coupon.repository;

import com.sparta.msa_project_part_3.domain.coupon.entity.Coupon;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    // 전체 목록 조회 (소프트 딜리트 제외)
    Page<Coupon> findAllByIsDeletedFalse(Pageable pageable);

    // 유효한 쿠폰 목록 조회 (현재 날짜가 유효기간 안에 있는 것)
    @Query("SELECT c FROM Coupon c WHERE c.isDeleted = false " +
            "AND c.startDate <= :now AND c.endDate >= :now")
    Page<Coupon> findAllActive(
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    // 상품 최대 할인율 조회용
    // 유효기간, 사용한도, 최소주문금액 조건 모두 체크
    @Query("SELECT c FROM Coupon c WHERE c.isDeleted = false " +
            "AND c.startDate <= :now AND c.endDate >= :now " +
            "AND (c.usageLimit IS NULL OR c.usedCount < c.usageLimit) " +
            "AND (c.minOrderAmount IS NULL OR c.minOrderAmount <= :productPrice)")
    List<Coupon> findApplicableCoupons(
            @Param("now") LocalDateTime now,
            @Param("productPrice") BigDecimal productPrice
    );
}