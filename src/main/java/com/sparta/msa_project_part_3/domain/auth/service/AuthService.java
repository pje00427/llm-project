package com.sparta.msa_project_part_3.domain.auth.service;

import com.sparta.msa_project_part_3.domain.auth.dto.request.LoginRequest;
import com.sparta.msa_project_part_3.domain.auth.dto.request.RegistrationRequest;
import com.sparta.msa_project_part_3.domain.auth.dto.response.LoginResponse;
import com.sparta.msa_project_part_3.domain.user.entity.User;
import com.sparta.msa_project_part_3.domain.user.repository.UserRepository;
import com.sparta.msa_project_part_3.global.exception.DomainException;
import com.sparta.msa_project_part_3.global.exception.DomainExceptionCode;
import com.sparta.msa_project_part_3.global.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  // 비밀번호 암호화/검증을 위한 인코더 - SecurityConfig에서 BCryptPasswordEncoder로 등록
  private final PasswordEncoder passwordEncoder;
  // 로그인 시 이메일/비밀번호 검증을 담당 - CustomUserDetailsService를 내부적으로 호출
  private final AuthenticationManager authenticationManager;
  // 인증 정보를 HttpSession에 저장/조회하는 저장소 - SecurityConfig에서 HttpSessionSecurityContextRepository로 등록
  private final SecurityContextRepository securityContextRepository;

  @Transactional
  public void registration(RegistrationRequest request) {
    // 이메일 중복 체크 - 이미 존재하면 예외 발생
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new DomainException(DomainExceptionCode.DUPLICATE_EMAIL);
    }

    // 비밀번호 암호화 후 저장
    userRepository.save(User.builder()
        .name(request.getName())
        .phone(request.getPhone())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .gender(request.getGender())
        .build());
  }

  @Transactional
  public LoginResponse login(LoginRequest loginRequest, HttpServletRequest request,
      HttpServletResponse response) {
    // authenticationManager가 내부적으로 CustomUserDetailsService.loadUserByUsername() 호출
    // DB에서 사용자 조회 후 비밀번호 검증
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getEmail(),
            loginRequest.getPassword()
        )
    );

    // 인증 정보를 SecurityContext에 저장
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);

    // SecurityContext를 HttpSession에 저장 → 이후 요청에서 세션으로 인증 유지
    securityContextRepository.saveContext(context, request, response);

    // 인증된 사용자 정보를 CustomUserDetails에서 꺼내서 응답 반환
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    return LoginResponse.builder()
        .userId(userDetails.getUserId())
        .email(userDetails.getEmail())
        .build();
  }

  // 내 정보 조회 - SecurityContext에서 인증 정보 꺼내서 반환
  public LoginResponse getLoginInfo(Authentication authentication) {
    try {
      Object principal = authentication.getPrincipal();
      Long userId = (Long) principal.getClass().getMethod("getUserId").invoke(principal);
      String email = (String) principal.getClass().getMethod("getEmail").invoke(principal);
      return LoginResponse.builder()
          .userId(userId)
          .email(email)
          .build();
    } catch (Exception e) {
      throw new DomainException(DomainExceptionCode.NOT_FOUND_USER);
    }
  }
}