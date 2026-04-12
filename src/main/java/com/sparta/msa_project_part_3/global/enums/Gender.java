package com.sparta.msa_project_part_3.global.enums;

// 사용자 성별을 나타내는 열거형
// DB에 "MALE", "FEMALE" 문자열로 저장됨 (User 엔티티에서 @Enumerated(EnumType.STRING) 사용)
public enum Gender {
  MALE,
  FEMALE
}