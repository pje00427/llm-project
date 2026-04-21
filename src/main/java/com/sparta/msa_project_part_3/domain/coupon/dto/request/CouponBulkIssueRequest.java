package com.sparta.msa_project_part_3.domain.coupon.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class CouponBulkIssueRequest {

    Long couponId;

    @Max(value = 1000, message = "최대 1000개까지만 발급 가능합니다.")
    @Min(value = 1, message = "최소 1개 이상 발급해야 합니다.")
    Integer issueCount;
}