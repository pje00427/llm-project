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
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class CartService {

  private final CartItemRepository cartItemRepository;
  private final ProductRepository productRepository;
  // [추가] afterCommit에서 캐시 무효화하기 위해 CacheManager 주입
  private final CacheManager cacheManager;

  // 장바구니 전체 조회
  // userId별로 캐시 키가 다르게 저장됨 → "cart::1", "cart::2"
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
  // [수정] @CacheEvict 제거 → DB 커밋 완료 후 afterCommit에서 캐시 삭제
  // 기존: 커밋 전 캐시 삭제 → 정합성 문제 가능
  // 개선: 커밋 완료 후 캐시 삭제 → 항상 최신 데이터 보장
  @Transactional
  public CartResponse addCartItem(Long userId, CartAddRequest request) {
    CartItem cartItem = cartItemRepository
        .findByUserIdAndProductId(userId, request.getProductId())
        .orElse(null);

    if (cartItem != null) {
      cartItem.addQuantity(request.getQuantity());
    } else {
      cartItem = cartItemRepository.save(CartItem.builder()
          .userId(userId)
          .productId(request.getProductId())
          .quantity(request.getQuantity())
          .build());
    }

    // DB 커밋 완료 후 캐시 삭제
    evictCacheAfterCommit(userId);

    return CartResponse.builder()
        .cartItemId(cartItem.getId())
        .productId(cartItem.getProductId())
        .quantity(cartItem.getQuantity())
        .build();
  }

  // 장바구니 상품 수량 수정
  // [수정] @CacheEvict 제거 → DB 커밋 완료 후 afterCommit에서 캐시 삭제
  @Transactional
  public CartResponse updateCartItem(Long userId, Long productId, CartUpdateRequest request) {
    CartItem cartItem = cartItemRepository
        .findByUserIdAndProductId(userId, productId)
        .orElseThrow(() -> new DomainException(DomainExceptionCode.NOT_FOUND_CART_ITEM));

    cartItem.updateQuantity(request.getQuantity());

    // DB 커밋 완료 후 캐시 삭제
    evictCacheAfterCommit(userId);

    return CartResponse.builder()
        .cartItemId(cartItem.getId())
        .productId(cartItem.getProductId())
        .quantity(cartItem.getQuantity())
        .build();
  }

  // 장바구니 상품 삭제
  // [수정] @CacheEvict 제거 → DB 커밋 완료 후 afterCommit에서 캐시 삭제
  @Transactional
  public void deleteCartItem(Long userId, Long productId) {
    CartItem cartItem = cartItemRepository
        .findByUserIdAndProductId(userId, productId)
        .orElseThrow(() -> new DomainException(DomainExceptionCode.NOT_FOUND_CART_ITEM));

    cartItemRepository.delete(cartItem);

    // DB 커밋 완료 후 캐시 삭제
    evictCacheAfterCommit(userId);
  }

  // DB 커밋 완료 후 캐시 삭제하는 공통 메서드
  // TransactionSynchronization.afterCommit(): 트랜잭션 커밋 완료 후 실행되는 훅
  // @CacheEvict는 커밋 전에 실행될 수 있어서 정합성 문제가 생길 수 있음
  // afterCommit을 사용하면 커밋 완료 후 캐시를 삭제하므로 항상 최신 데이터 보장
  private void evictCacheAfterCommit(Long userId) {
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCommit() {
            cacheManager.getCache("cart").evict(userId);
          }
        }
    );
  }
}