package com.sparta.msa_project_part_3.domain.auth.controller;

import com.sparta.msa_project_part_3.domain.auth.dto.request.LoginRequest;
import com.sparta.msa_project_part_3.domain.auth.dto.request.RegistrationRequest;
import com.sparta.msa_project_part_3.domain.auth.dto.response.LoginResponse;
import com.sparta.msa_project_part_3.domain.auth.service.AuthService;
import com.sparta.msa_project_part_3.global.exception.DomainException;
import com.sparta.msa_project_part_3.global.exception.DomainExceptionCode;
import com.sparta.msa_project_part_3.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  // 회원가입 API - 인증 없이 접근 가능 (SecurityConfig EXCLUDE_PATHS에 /api/auth/** 포함)
  @PostMapping("/registration")
  public ResponseEntity<ApiResponse<Void>> registration(@RequestBody RegistrationRequest request) {
    authService.registration(request);
    return ApiResponse.ok();
  }

  // 로그인 API - 인증 성공 시 HttpSession에 사용자 정보 저장
  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest,
      HttpServletRequest request, HttpServletResponse response) {
    LoginResponse loginResponse = authService.login(loginRequest, request, response);
    return ApiResponse.ok(loginResponse);
  }

  // 내 정보 조회 API - 세션에서 인증 정보 꺼내서 반환
  // 로그인하지 않은 경우 예외 처리
  @GetMapping("/status")
  public ResponseEntity<ApiResponse<LoginResponse>> checkStatus(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated() ||
        authentication instanceof AnonymousAuthenticationToken) {
      throw new DomainException(DomainExceptionCode.NOT_FOUND_USER);
    }
    return ApiResponse.ok(authService.getLoginInfo(authentication));
  }

  // 로그아웃 API - SecurityContext 초기화 + 세션 무효화
  // HttpSession 방식으로 처리 (세션 삭제 = 로그아웃)
  @GetMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
    // SecurityContext에서 인증 정보 제거
    SecurityContextHolder.clearContext();
    // 세션 무효화 - 세션에 저장된 모든 데이터 삭제
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }
    return ApiResponse.ok();
  }
}