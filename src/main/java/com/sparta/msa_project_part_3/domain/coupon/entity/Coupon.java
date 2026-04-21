package com.sparta.msa_project_part_3.domain.coupon.entity;

import com.sparta.msa_project_part_3.global.entity.BaseEntity;
import com.sparta.msa_project_part_3.global.enums.DiscountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Table(name = "coupon")
@Entity
@Getter
@DynamicInsert  // null 필드는 INSERT 쿼리에서 제외
@DynamicUpdate  // 변경된 필드만 UPDATE 쿼리에 포함
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity { // createdAt, updatedAt 자동 관리

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;  // 쿠폰 고유 ID (자동 증가)

    @Column(nullable = false)
    String couponName;  // 쿠폰명 (예: 봄 신상 20%)

    @Enumerated(EnumType.STRING)  // DB에 "PERCENTAGE", "FIXED" 문자열로 저장
    @Column(nullable = false)
    DiscountType discountType;  // 할인 타입 (PERCENTAGE:정률, FIXED:정액)

    @Column(nullable = false)
    BigDecimal discountValue;  // 할인값 (PERCENTAGE면 20.00, FIXED면 1000.00)

    BigDecimal minOrderAmount;      // 최소 주문 금액 (이 금액 이상 주문 시에만 사용 가능, null이면 제한 없음)
    BigDecimal maxDiscountAmount;   // 최대 할인 금액 (PERCENTAGE일 때 상한선, null이면 제한 없음)
    // 예: 20% 할인인데 최대 5000원까지만 할인

    @Column(nullable = false)
    LocalDateTime startDate;  // 쿠폰 사용 시작일

    @Column(nullable = false)
    LocalDateTime endDate;    // 쿠폰 사용 종료일

    Integer usageLimit;  // 총 발급 한도 (null이면 무제한)
    // 예: 100이면 100개까지만 발급 가능

    @Column(nullable = false)
    Integer usedCount;   // 현재까지 사용된 횟수 (기본값 0)
    // usedCount < usageLimit 이어야 사용 가능

    @Column(nullable = false)
    Boolean isDeleted;   // 소프트 딜리트 여부 (기본값 false)
    // true면 삭제된 쿠폰으로 조회에서 제외
    @Builder
    public Coupon(String couponName, DiscountType discountType,
                  BigDecimal discountValue, BigDecimal minOrderAmount,
                  BigDecimal maxDiscountAmount, LocalDateTime startDate,
                  LocalDateTime endDate, Integer usageLimit) {
        this.couponName = couponName;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderAmount = minOrderAmount;
        this.maxDiscountAmount = maxDiscountAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.usageLimit = usageLimit;
        this.usedCount = 0;
        this.isDeleted = false;
    }

    // 소프트 딜리트
    public void delete() {
        this.isDeleted = true;
    }
}