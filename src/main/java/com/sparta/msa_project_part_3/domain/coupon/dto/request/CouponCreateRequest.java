package com.sparta.msa_project_part_3.domain.coupon.dto.request;

import com.sparta.msa_project_part_3.global.enums.DiscountType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class CouponCreateRequest {

    String couponName;
    DiscountType discountType;
    BigDecimal discountValue;
    BigDecimal minOrderAmount;
    BigDecimal maxDiscountAmount;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Integer usageLimit;
}