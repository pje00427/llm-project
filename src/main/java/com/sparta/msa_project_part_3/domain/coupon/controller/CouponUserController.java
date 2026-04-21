package com.sparta.msa_project_part_3.domain.coupon.controller;

import com.sparta.msa_project_part_3.domain.coupon.dto.response.CouponUserResponse;
import com.sparta.msa_project_part_3.domain.coupon.service.CouponService;
import com.sparta.msa_project_part_3.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons/users")
public class CouponUserController {

    private final CouponService couponService;

    // 특정 유저 쿠폰 목록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<CouponUserResponse>>> getUserCoupons(
            @PathVariable Long userId) {
        return ApiResponse.ok(couponService.getUserCoupons(userId));
    }

    // 쿠폰 삭제
    @DeleteMapping("/{couponUserId}")
    public ResponseEntity<ApiResponse<Void>> deleteCouponUser(
            @PathVariable Long couponUserId) {
        couponService.deleteCouponUser(couponUserId);
        return ApiResponse.ok();
    }

    // 쿠폰 사용 처리
    @PatchMapping("/{couponUserId}/use")
    public ResponseEntity<ApiResponse<Void>> useCoupon(
            @PathVariable Long couponUserId) {
        couponService.useCoupon(couponUserId);
        return ApiResponse.ok();
    }
}