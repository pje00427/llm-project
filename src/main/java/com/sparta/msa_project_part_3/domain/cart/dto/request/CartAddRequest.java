package com.sparta.msa_project_part_3.domain.cart.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

// 장바구니 상품 추가/수정 요청 DTO
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartAddRequest {

  // 담을 상품 ID
  Long productId;

  // 담을 수량
  Integer quantity;
}