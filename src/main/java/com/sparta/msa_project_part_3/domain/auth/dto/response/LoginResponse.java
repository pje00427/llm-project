package com.sparta.msa_project_part_3.domain.auth.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

// 로그인 성공 시 클라이언트에게 반환하는 데이터
// userId와 email만 반환 (비밀번호 등 민감정보 제외)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginResponse {

  // 로그인된 사용자 ID - 장바구니 등 사용자별 기능에서 활용
  Long userId;

  // 로그인된 사용자 이메일
  String email;
}