package com.sparta.msa_project_part_3.domain.cart.repository;

import com.sparta.msa_project_part_3.domain.cart.entity.CartItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// CartItem 엔티티의 DB 접근을 담당
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

  // 특정 사용자의 장바구니 전체 조회
  List<CartItem> findByUserId(Long userId);

  // 특정 사용자의 특정 상품 조회
  // 장바구니 추가 시 이미 담긴 상품인지 확인할 때 사용
  Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);
}