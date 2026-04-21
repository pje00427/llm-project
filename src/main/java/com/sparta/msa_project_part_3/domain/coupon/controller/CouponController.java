package com.sparta.msa_project_part_3.domain.coupon.controller;

import com.sparta.msa_project_part_3.domain.coupon.dto.request.CouponBulkIssueRequest;
import com.sparta.msa_project_part_3.domain.coupon.dto.request.CouponCreateRequest;
import com.sparta.msa_project_part_3.domain.coupon.dto.request.CouponRegisterRequest;
import com.sparta.msa_project_part_3.domain.coupon.dto.response.CouponResponse;
import com.sparta.msa_project_part_3.domain.coupon.dto.response.MaxDiscountResponse;
import com.sparta.msa_project_part_3.domain.coupon.service.CouponService;
import com.sparta.msa_project_part_3.global.response.ApiResponse;
import com.sparta.msa_project_part_3.global.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    // 쿠폰 단일 조회
    @GetMapping("/{couponId}")
    public ResponseEntity<ApiResponse<CouponResponse>> getCoupon(
            @PathVariable Long couponId) {
        return ApiResponse.ok(couponService.getCoupon(couponId));
    }

    // 쿠폰 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CouponResponse>>> getCoupons(
            Pageable pageable,
            @RequestParam(required = false) Boolean isActive) {
        return ApiResponse.ok(new PageResponse<>(couponService.getCoupons(pageable, isActive)));
    }

    // 쿠폰 생성
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createCoupon(
            @RequestBody CouponCreateRequest request) {
        couponService.createCoupon(request);
        return ApiResponse.ok();
    }

    // 쿠폰 삭제 (소프트 딜리트)
    @DeleteMapping("/{couponId}")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(
            @PathVariable Long couponId) {
        couponService.deleteCoupon(couponId);
        return ApiResponse.ok();
    }

    // 쿠폰 대량 발급
    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<Void>> bulkIssueCoupons(
            @RequestBody @Valid CouponBulkIssueRequest request) {
        couponService.bulkIssueCoupons(request);
        return ApiResponse.ok();
    }

    // 쿠폰 등록
    @PostMapping("/issuance")
    public ResponseEntity<ApiResponse<Void>> registerCoupon(
            @RequestBody CouponRegisterRequest request) {
        couponService.registerCoupon(request);
        return ApiResponse.ok();
    }
}