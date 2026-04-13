package com.sparta.msa_project_part_3.domain.cart.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

// 장바구니 상품 수량 수정 요청 DTO
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartUpdateRequest {

  // 변경할 수량
  Integer quantity;
}