package com.sparta.msa_project_part_3.domain.coupon.dto.request;

import lombok.Getter;

@Getter
public class CouponRegisterRequest {

    String couponCode;  // 등록할 쿠폰 코드
    Long userId;        // 등록하는 사용자 ID
}