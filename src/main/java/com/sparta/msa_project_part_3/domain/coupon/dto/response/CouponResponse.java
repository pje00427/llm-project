package com.sparta.msa_project_part_3.domain.coupon.dto.response;

import com.sparta.msa_project_part_3.domain.coupon.entity.Coupon;
import com.sparta.msa_project_part_3.global.enums.DiscountType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class CouponResponse {

    Long id;
    String couponName;
    DiscountType discountType;
    BigDecimal discountValue;
    BigDecimal minOrderAmount;
    BigDecimal maxDiscountAmount;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Integer usageLimit;
    Integer usedCount;

    // 엔티티 → DTO 변환
    public static CouponResponse from(Coupon coupon) {
        CouponResponse response = new CouponResponse();
        response.id = coupon.getId();
        response.couponName = coupon.getCouponName();
        response.discountType = coupon.getDiscountType();
        response.discountValue = coupon.getDiscountValue();
        response.minOrderAmount = coupon.getMinOrderAmount();
        response.maxDiscountAmount = coupon.getMaxDiscountAmount();
        response.startDate = coupon.getStartDate();
        response.endDate = coupon.getEndDate();
        response.usageLimit = coupon.getUsageLimit();
        response.usedCount = coupon.getUsedCount();
        return response;
    }
}