package com.sparta.msa_project_part_3.domain.auth.dto.request;

import com.sparta.msa_project_part_3.global.enums.Gender;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

// 회원가입 요청 시 클라이언트에서 전달받는 데이터
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationRequest {

  // 사용자 이름
  String name;

  // 전화번호
  String phone;

  // 이메일 - 중복 체크 후 저장
  String email;

  // 평문 비밀번호 - AuthService에서 BCrypt로 암호화 후 저장
  String password;

  // 성별
  Gender gender;
}