package com.sparta.msa_project_part_3.domain.cart.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {

  Long cartItemId;

  Long productId;

  Integer quantity;

  // 상품 정보 조인 결과 - 도전사항
  String productName;

  Integer productPrice;
}