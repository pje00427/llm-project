package com.sparta.msa_project_part_3.domain.cart.controller;

import com.sparta.msa_project_part_3.domain.cart.dto.request.CartAddRequest;
import com.sparta.msa_project_part_3.domain.cart.dto.request.CartUpdateRequest;
import com.sparta.msa_project_part_3.domain.cart.dto.response.CartResponse;
import com.sparta.msa_project_part_3.domain.cart.service.CartService;
import com.sparta.msa_project_part_3.global.exception.DomainException;
import com.sparta.msa_project_part_3.global.exception.DomainExceptionCode;
import com.sparta.msa_project_part_3.global.response.ApiResponse;
import com.sparta.msa_project_part_3.global.security.CustomUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

  private final CartService cartService;

  // 세션에서 userId 추출하는 공통 메서드
  // Spring Security Authentication 객체에서 CustomUserDetails를 꺼내 userId 반환
  private Long getUserId(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new DomainException(DomainExceptionCode.NOT_FOUND_USER);
    }
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return userDetails.getUserId();
  }

  // 장바구니 전체 조회
  @GetMapping
  public ResponseEntity<ApiResponse<List<CartResponse>>> getCartItems(Authentication authentication) {
    Long userId = getUserId(authentication);
    return ApiResponse.ok(cartService.getCartItems(userId));
  }

  // 장바구니 상품 추가
  @PostMapping
  public ResponseEntity<ApiResponse<CartResponse>> addCartItem(
      Authentication authentication,
      @RequestBody CartAddRequest request) {
    Long userId = getUserId(authentication);
    return ApiResponse.ok(cartService.addCartItem(userId, request));
  }

  // 장바구니 상품 수량 수정
  @PutMapping("/{productId}")
  public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
      Authentication authentication,
      @PathVariable Long productId,
      @RequestBody CartUpdateRequest request) {
    Long userId = getUserId(authentication);
    return ApiResponse.ok(cartService.updateCartItem(userId, productId, request));
  }

  // 장바구니 상품 삭제
  @DeleteMapping("/{productId}")
  public ResponseEntity<ApiResponse<Void>> deleteCartItem(
      Authentication authentication,
      @PathVariable Long productId) {
    Long userId = getUserId(authentication);
    cartService.deleteCartItem(userId, productId);
    return ApiResponse.ok();
  }
}