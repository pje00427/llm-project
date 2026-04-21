package com.sparta.msa_project_part_3.domain.coupon.dto.response;

import com.sparta.msa_project_part_3.domain.coupon.entity.CouponUser;
import com.sparta.msa_project_part_3.global.enums.CouponStatus;
import lombok.Getter;

@Getter
public class CouponUserResponse {

    Long id;             // coupon_user ID
    Long couponId;       // 쿠폰 마스터 ID
    String couponName;   // 쿠폰명
    Long userId;         // 등록한 유저 ID
    String code;         // 쿠폰 코드
    CouponStatus status; // 쿠폰 상태

    public static CouponUserResponse from(CouponUser couponUser) {
        CouponUserResponse response = new CouponUserResponse();
        response.id = couponUser.getId();
        response.couponId = couponUser.getCoupon().getId();
        response.couponName = couponUser.getCoupon().getCouponName();
        response.userId = couponUser.getUserId();
        response.code = couponUser.getCode();
        response.status = couponUser.getStatus();
        return response;
    }
}