package com.sparta.msa_project_part_3.domain.cart.entity;

import com.sparta.msa_project_part_3.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

// 장바구니 아이템 엔티티
// userId + productId 조합으로 사용자별 장바구니 관리
@Table(name = "cart_items")
@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  // 세션에서 추출한 userId - 누구의 장바구니인지 식별
  @Column(nullable = false)
  Long userId;

  // 담은 상품 ID
  @Column(nullable = false)
  Long productId;

  // 상품 수량
  @Column(nullable = false)
  Integer quantity;

  @Builder
  public CartItem(Long userId, Long productId, Integer quantity) {
    this.userId = userId;
    this.productId = productId;
    this.quantity = quantity;
  }

  // 수량 증가 - 이미 담긴 상품 추가 시 사용
  public void addQuantity(Integer quantity) {
    this.quantity += quantity;
  }

  // 수량 수정
  public void updateQuantity(Integer quantity) {
    this.quantity = quantity;
  }
}