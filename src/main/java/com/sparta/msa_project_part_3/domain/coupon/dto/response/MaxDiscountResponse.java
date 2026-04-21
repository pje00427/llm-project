package com.sparta.msa_project_part_3.domain.coupon.dto.response;

import com.sparta.msa_project_part_3.domain.coupon.entity.Coupon;
import com.sparta.msa_project_part_3.global.enums.DiscountType;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class MaxDiscountResponse {

    Long couponId;
    String couponName;
    DiscountType discountType;
    BigDecimal discountValue;
    BigDecimal maxDiscountAmount;

    public static MaxDiscountResponse from(Coupon coupon) {
        MaxDiscountResponse response = new MaxDiscountResponse();
        response.couponId = coupon.getId();
        response.couponName = coupon.getCouponName();
        response.discountType = coupon.getDiscountType();
        response.discountValue = coupon.getDiscountValue();
        response.maxDiscountAmount = coupon.getMaxDiscountAmount();
        return response;
    }
}