package com.sparta.msa_project_part_3.domain.cart.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

// 장바구니 조회 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {

  Long cartItemId;

  // 상품 ID
  Long productId;

  // 수량
  Integer quantity;
}