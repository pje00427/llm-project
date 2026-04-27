package com.sparta.msa_project_part_3.domain.coupon.repository;

import com.sparta.msa_project_part_3.domain.coupon.entity.CouponUser;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponUserRepository extends JpaRepository<CouponUser, Long> {

    // 쿠폰 코드로 조회
    // 비관적 락 제거 → 낙관적 락으로 변경
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT cu FROM CouponUser cu JOIN FETCH cu.coupon WHERE cu.code = :code")
    Optional<CouponUser> findByCode(@Param("code") String code);

    // 특정 유저의 쿠폰 목록 조회 (coupon 정보 함께)
    @Query("SELECT cu FROM CouponUser cu JOIN FETCH cu.coupon WHERE cu.userId = :userId")
    List<CouponUser> findAllByUserId(@Param("userId") Long userId);
}