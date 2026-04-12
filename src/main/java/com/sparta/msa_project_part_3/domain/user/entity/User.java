package com.sparta.msa_project_part_3.domain.user.entity;

import com.sparta.msa_project_part_3.global.entity.BaseEntity;
import com.sparta.msa_project_part_3.global.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

// 사용자 정보를 저장하는 엔티티
// BaseEntity를 상속받아 createdAt, updatedAt 자동 관리
// DynamicInsert: null 필드는 INSERT 쿼리에서 제외 (불필요한 쿼리 최적화)
// DynamicUpdate: 변경된 필드만 UPDATE 쿼리에 포함 (불필요한 쿼리 최적화)
@Table(name = "users")
@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  // 사용자 이름
  @Column(nullable = false)
  String name;

  // 전화번호
  @Column(nullable = false)
  String phone;

  // 이메일 - unique 제약조건으로 중복 가입 방지
  @Column(nullable = false, unique = true)
  String email;

  // 비밀번호 - BCrypt로 암호화되어 저장됨
  @Column(nullable = false)
  String password;

  // 성별 - DB에 "MALE", "FEMALE" 문자열로 저장
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  Gender gender;

  // 외부에서 User 객체 생성 시 Builder 패턴 사용
  // AuthService에서 회원가입 시 사용
  @Builder
  public User(String name, String phone, String password, String email, Gender gender) {
    this.name = name;
    this.phone = phone;
    this.password = password;
    this.email = email;
    this.gender = gender;
  }
}