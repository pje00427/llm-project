package com.sparta.msa_project_part_3.domain.cart.controller;

import com.sparta.msa_project_part_3.domain.cart.dto.request.CartAddRequest;
import com.sparta.msa_project_part_3.domain.cart.dto.request.CartUpdateRequest;
import com.sparta.msa_project_part_3.domain.cart.dto.response.CartResponse;
import com.sparta.msa_project_part_3.domain.cart.service.CartService;
import com.sparta.msa_project_part_3.domain.cart.service.GuestCartService;
import com.sparta.msa_project_part_3.global.exception.DomainException;
import com.sparta.msa_project_part_3.global.exception.DomainExceptionCode;
import com.sparta.msa_project_part_3.global.response.ApiResponse;
import com.sparta.msa_project_part_3.global.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
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
  // [추가] 비회원 장바구니 서비스
  private final GuestCartService guestCartService;

  // 로그인 여부 확인 공통 메서드
  // [추가] AnonymousAuthenticationToken 체크 - 비로그인 사용자 식별
  private boolean isAuthenticated(Authentication authentication) {
    return authentication != null
            && authentication.isAuthenticated()
            && !(authentication instanceof AnonymousAuthenticationToken);
  }

  // 세션에서 userId 추출하는 공통 메서드
  private Long getUserId(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return userDetails.getUserId();
  }

  // 장바구니 전체 조회
  // [수정] 로그인 여부에 따라 회원/비회원 장바구니 분기
  @GetMapping
  public ResponseEntity<ApiResponse<List<CartResponse>>> getCartItems(
          Authentication authentication,
          HttpServletRequest request) {
    if (isAuthenticated(authentication)) {
      // 로그인 → 회원 장바구니 조회 (DB)
      return ApiResponse.ok(cartService.getCartItems(getUserId(authentication)));
    }
    // 비로그인 → 비회원 장바구니 조회 (Redis)
    return ApiResponse.ok(guestCartService.getCartItems(request.getSession().getId()));
  }

  // 장바구니 상품 추가
  // [수정] 로그인 여부에 따라 회원/비회원 장바구니 분기
  @PostMapping
  public ResponseEntity<ApiResponse<List<CartResponse>>> addCartItem(
          Authentication authentication,
          HttpServletRequest request,
          @RequestBody CartAddRequest cartRequest) {
    if (isAuthenticated(authentication)) {
      // 로그인 → 회원 장바구니 추가 (DB)
      cartService.addCartItem(getUserId(authentication), cartRequest);
      return ApiResponse.ok(cartService.getCartItems(getUserId(authentication)));
    }
    // 비로그인 → 비회원 장바구니 추가 (Redis)
    return ApiResponse.ok(guestCartService.addCartItem(
            request.getSession().getId(),
            cartRequest.getProductId(),
            cartRequest.getQuantity()));
  }

  // 장바구니 상품 수량 수정
  // [수정] 로그인 여부에 따라 회원/비회원 장바구니 분기
  @PutMapping("/{productId}")
  public ResponseEntity<ApiResponse<List<CartResponse>>> updateCartItem(
          Authentication authentication,
          HttpServletRequest request,
          @PathVariable Long productId,
          @RequestBody CartUpdateRequest cartRequest) {
    if (isAuthenticated(authentication)) {
      // 로그인 → 회원 장바구니 수정 (DB)
      cartService.updateCartItem(getUserId(authentication), productId, cartRequest);
      return ApiResponse.ok(cartService.getCartItems(getUserId(authentication)));
    }
    // 비로그인 → 비회원 장바구니 수정 (Redis)
    return ApiResponse.ok(guestCartService.updateCartItem(
            request.getSession().getId(),
            productId,
            cartRequest.getQuantity()));
  }

  // 장바구니 상품 삭제
  // [수정] 로그인 여부에 따라 회원/비회원 장바구니 분기
  @DeleteMapping("/{productId}")
  public ResponseEntity<ApiResponse<Void>> deleteCartItem(
          Authentication authentication,
          HttpServletRequest request,
          @PathVariable Long productId) {
    if (isAuthenticated(authentication)) {
      // 로그인 → 회원 장바구니 삭제 (DB)
      cartService.deleteCartItem(getUserId(authentication), productId);
    } else {
      // 비로그인 → 비회원 장바구니 삭제 (Redis)
      guestCartService.deleteCartItem(request.getSession().getId(), productId);
    }
    return ApiResponse.ok();
  }
}