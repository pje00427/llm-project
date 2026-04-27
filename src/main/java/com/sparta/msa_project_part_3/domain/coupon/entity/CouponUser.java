package com.sparta.msa_project_part_3.domain.coupon.entity;

import com.sparta.msa_project_part_3.global.entity.BaseEntity;
import com.sparta.msa_project_part_3.global.enums.CouponStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import jakarta.persistence.Version;

@Table(name = "coupon_user")
@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;  // coupon_user 테이블의 고유 ID (발급 레코드 ID)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    Coupon coupon;  // 어떤 쿠폰인지 (coupon 테이블 참조)

    Long userId;  // 쿠폰 등록한 사용자 ID (미등록 시 NULL)

    @Column(unique = true, nullable = false)
    String code;  // 쿠폰 코드 (UUID, 중복 불가)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    CouponStatus status;  // 쿠폰 상태 (UNREGISTERED:미등록, REGISTERED:등록완료, USED:사용완료)

    @Builder
    public CouponUser(Coupon coupon, String code) {
        this.coupon = coupon;
        this.code = code;
        this.status = CouponStatus.UNREGISTERED; // 발급 시 기본값
    }

    // 쿠폰 등록 - userId 설정 + status REGISTERED로 변경
    public void register(Long userId) {
        this.userId = userId;
        this.status = CouponStatus.REGISTERED;
    }
    // 쿠폰 사용 처리
    public void use() {
        this.status = CouponStatus.USED;
    }
}