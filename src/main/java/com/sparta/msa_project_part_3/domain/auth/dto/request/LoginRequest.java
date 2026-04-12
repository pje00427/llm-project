package com.sparta.msa_project_part_3.domain.auth.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

// 로그인 요청 시 클라이언트에서 전달받는 데이터
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {

  // 로그인 식별자로 사용
  String email;

  // 암호화 전 평문 비밀번호 - AuthService에서 BCrypt로 검증
  String password;
}