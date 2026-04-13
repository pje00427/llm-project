package com.sparta.msa_project_part_3.domain.cart.service;

import com.sparta.msa_project_part_3.domain.cart.dto.request.CartAddRequest;
import com.sparta.msa_project_part_3.domain.cart.dto.request.CartUpdateRequest;
import com.sparta.msa_project_part_3.domain.cart.dto.response.CartResponse;
import com.sparta.msa_project_part_3.domain.cart.entity.CartItem;
import com.sparta.msa_project_part_3.domain.cart.repository.CartItemRepository;
import com.sparta.msa_project_part_3.domain.product.entity.Product;
import com.sparta.msa_project_part_3.domain.product.repository.ProductRepository;
import com.sparta.msa_project_part_3.global.exception.DomainException;
import com.sparta.msa_project_part_3.global.exception.DomainExceptionCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

  private final CartItemRepository cartItemRepository;
  private final ProductRepository productRepository;

  // 장바구니 전체 조회
  // [캐싱 추가] userId별로 캐시 키가 다르게 저장됨 → "cart::1", "cart::2"
  // 캐시가 있으면 DB 조회 없이 Redis에서 바로 반환
  // 캐시가 없으면 DB 조회 후 Redis에 저장
  @Cacheable(value = "cart", key = "#userId")
  @Transactional(readOnly = true)
  public List<CartResponse> getCartItems(Long userId) {
    return cartItemRepository.findByUserId(userId)
        .stream()
        .map(item -> {
          Product product = productRepository.findById(item.getProductId())
              .orElse(null);

          return CartResponse.builder()
              .cartItemId(item.getId())
              .productId(item.getProductId())
              .quantity(item.getQuantity())
              .productName(product != null ? product.getName() : null)
              .productPrice(product != null ? product.getPrice() : null)
              .build();
        })
        .toList();
  }

  // 장바구니 상품 추가
  // 이미 담긴 상품이면 수량만 증가, 없으면 새로 추가
  // [캐싱 추가] 장바구니 변경 시 해당 사용자 캐시 삭제 → 다음 조회 시 최신 데이터 반영
  @CacheEvict(value = "cart", key = "#userId")
  @Transactional
  public CartResponse addCartItem(Long userId, CartAddRequest request) {
    CartItem cartItem = cartItemRepository
        .findByUserIdAndProductId(userId, request.getProductId())
        .orElse(null);

    if (cartItem != null) {
      // 이미 담긴 상품 → 수량만 증가
      cartItem.addQuantity(request.getQuantity());
    } else {
      // 새 상품 → 새로 추가
      cartItem = cartItemRepository.save(CartItem.builder()
          .userId(userId)
          .productId(request.getProductId())
          .quantity(request.getQuantity())
          .build());
    }

    return CartResponse.builder()
        .cartItemId(cartItem.getId())
        .productId(cartItem.getProductId())
        .quantity(cartItem.getQuantity())
        .build();
  }

  // 장바구니 상품 수량 수정
  // [캐싱 추가] 수량 변경 시 해당 사용자 캐시 삭제 → 다음 조회 시 최신 데이터 반영
  @CacheEvict(value = "cart", key = "#userId")
  @Transactional
  public CartResponse updateCartItem(Long userId, Long productId, CartUpdateRequest request) {
    CartItem cartItem = cartItemRepository
        .findByUserIdAndProductId(userId, productId)
        .orElseThrow(() -> new DomainException(DomainExceptionCode.NOT_FOUND_CART_ITEM));

    cartItem.updateQuantity(request.getQuantity());

    return CartResponse.builder()
        .cartItemId(cartItem.getId())
        .productId(cartItem.getProductId())
        .quantity(cartItem.getQuantity())
        .build();
  }

  // 장바구니 상품 삭제
  // [캐싱 추가] 상품 삭제 시 해당 사용자 캐시 삭제 → 다음 조회 시 최신 데이터 반영
  @CacheEvict(value = "cart", key = "#userId")
  @Transactional
  public void deleteCartItem(Long userId, Long productId) {
    CartItem cartItem = cartItemRepository
        .findByUserIdAndProductId(userId, productId)
        .orElseThrow(() -> new DomainException(DomainExceptionCode.NOT_FOUND_CART_ITEM));

    cartItemRepository.delete(cartItem);
  }
}