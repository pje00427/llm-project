package com.sparta.msa_project_part_3.domain.coupon.service;

import com.sparta.msa_project_part_3.domain.coupon.dto.request.CouponBulkIssueRequest;
import com.sparta.msa_project_part_3.domain.coupon.dto.request.CouponCreateRequest;
import com.sparta.msa_project_part_3.domain.coupon.dto.request.CouponRegisterRequest;
import com.sparta.msa_project_part_3.domain.coupon.dto.response.CouponResponse;
import com.sparta.msa_project_part_3.domain.coupon.dto.response.CouponUserResponse;
import com.sparta.msa_project_part_3.domain.coupon.dto.response.MaxDiscountResponse;
import com.sparta.msa_project_part_3.domain.coupon.entity.Coupon;
import com.sparta.msa_project_part_3.domain.coupon.entity.CouponUser;
import com.sparta.msa_project_part_3.domain.coupon.repository.CouponRepository;
import com.sparta.msa_project_part_3.domain.coupon.repository.CouponUserRepository;
import com.sparta.msa_project_part_3.domain.product.entity.Product;
import com.sparta.msa_project_part_3.domain.product.repository.ProductRepository;
import com.sparta.msa_project_part_3.global.enums.CouponStatus;
import com.sparta.msa_project_part_3.global.exception.DomainException;
import com.sparta.msa_project_part_3.global.exception.DomainExceptionCode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponUserRepository couponUserRepository;
    private final ProductRepository productRepository;

    // 쿠폰 단일 조회
    @Transactional(readOnly = true)
    public CouponResponse getCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .filter(c -> !c.getIsDeleted())
                .orElseThrow(() -> new DomainException(DomainExceptionCode.NOT_FOUND_COUPON));
        return CouponResponse.from(coupon);
    }

    // 쿠폰 목록 조회
    @Transactional(readOnly = true)
    public Page<CouponResponse> getCoupons(Pageable pageable, Boolean isActive) {
        if (isActive != null && isActive) {
            // 현재 유효한 쿠폰만 조회
            return couponRepository.findAllActive(LocalDateTime.now(), pageable)
                    .map(CouponResponse::from);
        }
        // 전체 목록 조회 (소프트 딜리트 제외)
        return couponRepository.findAllByIsDeletedFalse(pageable)
                .map(CouponResponse::from);
    }

    // 쿠폰 생성
    @Transactional
    public void createCoupon(CouponCreateRequest request) {
        Coupon coupon = Coupon.builder()
                .couponName(request.getCouponName())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minOrderAmount(request.getMinOrderAmount())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .usageLimit(request.getUsageLimit())
                .build();
        couponRepository.save(coupon);
    }

    // 쿠폰 삭제 (소프트 딜리트)
    @Transactional
    public void deleteCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .filter(c -> !c.getIsDeleted())
                .orElseThrow(() -> new DomainException(DomainExceptionCode.NOT_FOUND_COUPON));
        coupon.delete();
    }

    // 상품별 최대 할인율 조회
    @Transactional(readOnly = true)
    public MaxDiscountResponse getMaxDiscount(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.NOT_FOUND_PRODUCT));

        List<Coupon> coupons = couponRepository.findApplicableCoupons(
                LocalDateTime.now(),
                java.math.BigDecimal.valueOf(product.getPrice())  // Integer → BigDecimal 변환
        );

        // 최대 할인율 계산
        return coupons.stream()
                .max(Comparator.comparing(coupon -> {
                    if (coupon.getDiscountType() == com.sparta.msa_project_part_3.global.enums.DiscountType.PERCENTAGE) {
                        // 정률 할인: 실제 할인 금액 계산
                        java.math.BigDecimal discountAmount = java.math.BigDecimal.valueOf(product.getPrice())
                                .multiply(coupon.getDiscountValue())
                                .divide(java.math.BigDecimal.valueOf(100));
                        // maxDiscountAmount 적용
                        if (coupon.getMaxDiscountAmount() != null) {
                            discountAmount = discountAmount.min(coupon.getMaxDiscountAmount());
                        }
                        return discountAmount;
                    } else {
                        // 정액 할인
                        return coupon.getDiscountValue();
                    }
                }))
                .map(MaxDiscountResponse::from)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.NOT_FOUND_COUPON));
    }

    // 쿠폰 대량 발급
    @Transactional
    public void bulkIssueCoupons(CouponBulkIssueRequest request) {
        Coupon coupon = couponRepository.findById(request.getCouponId())
                .filter(c -> !c.getIsDeleted())
                .orElseThrow(() -> new DomainException(DomainExceptionCode.NOT_FOUND_COUPON));

        List<CouponUser> couponUsers = new ArrayList<>();
        for (int i = 0; i < request.getIssueCount(); i++) {
            String code = UUID.randomUUID().toString()
                    .replace("-", "")
                    .substring(0, 12)
                    .toUpperCase();

            couponUsers.add(CouponUser.builder()
                    .coupon(coupon)
                    .code(code)
                    .build());
        }

        // 한 번에 저장
        couponUserRepository.saveAll(couponUsers);
    }

    // 쿠폰 등록 (비관적 락으로 동시 등록 방지)
    @Transactional
    public void registerCoupon(CouponRegisterRequest request) {
        // 비관적 락으로 조회
        CouponUser couponUser = couponUserRepository.findByCode(request.getCouponCode())
                .orElseThrow(() -> new DomainException(DomainExceptionCode.INVALID_COUPON_CODE));

        // 참조하는 coupon이 삭제됐는지 체크 추가 ← 이 부분 추가!
        if (couponUser.getCoupon().getIsDeleted()) {
            throw new DomainException(DomainExceptionCode.INVALID_COUPON_CODE);
        }

        // 이미 등록된 쿠폰인지 확인
        if (couponUser.getStatus() != CouponStatus.UNREGISTERED) {
            throw new DomainException(DomainExceptionCode.ALREADY_REGISTERED_COUPON);
        }

        // 쿠폰 등록
        couponUser.register(request.getUserId());
    }

    // 특정 유저 쿠폰 목록 조회
    @Transactional(readOnly = true)
    public List<CouponUserResponse> getUserCoupons(Long userId) {
        return couponUserRepository.findAllByUserId(userId)
                .stream()
                .map(CouponUserResponse::from)
                .toList();
    }

    // 쿠폰 삭제
    @Transactional
    public void deleteCouponUser(Long couponUserId) {
        CouponUser couponUser = couponUserRepository.findById(couponUserId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.INVALID_COUPON_CODE));
        couponUserRepository.delete(couponUser);
    }
    // 쿠폰 사용하기
    @Transactional
    public void useCoupon(Long couponUserId) {
        // 사용할 쿠폰 조회
        CouponUser couponUser = couponUserRepository.findById(couponUserId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.INVALID_COUPON_CODE));

        // REGISTERED 상태인지 확인 (UNREGISTERED, USED 상태면 사용 불가)
        if (couponUser.getStatus() != CouponStatus.REGISTERED) {
            throw new DomainException(DomainExceptionCode.ALREADY_REGISTERED_COUPON);
        }

        // Coupon에 비관적 락 걸고 조회
        // 동시 사용 시 usedCount 업데이트 유실 방지
        Coupon coupon = couponRepository.findByIdForUpdate(couponUser.getCoupon().getId())
                .orElseThrow(() -> new DomainException(DomainExceptionCode.NOT_FOUND_COUPON));

        // 사용 한도 체크 (usageLimit이 null이면 무제한)
        if (coupon.getUsageLimit() != null
                && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new DomainException(DomainExceptionCode.COUPON_USAGE_LIMIT_EXCEEDED);
        }

        // 쿠폰 상태 USED로 변경
        couponUser.use();

        // 쿠폰 사용 횟수 증가
        coupon.increaseUsedCount();
    }
}