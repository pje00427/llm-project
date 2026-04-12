package com.sparta.msa_project_part_3.global.security;

import com.sparta.msa_project_part_3.domain.user.entity.User;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// Spring Security가 인증할 때 사용하는 사용자 정보 객체
// 기본 UserDetails에는 username, password만 있어서
// userId, email 등 추가 정보를 담기 위해 커스텀
// Serializable: Redis 세션에 저장될 때 직렬화 필요
@Getter
public class CustomUserDetails implements UserDetails, Serializable {

  private static final long serialVersionUID = 1L;

  // 우리가 추가로 필요한 사용자 정보
  private final Long userId;
  private final String email;
  private final String password;

  public CustomUserDetails(Long userId, String email, String password) {
    this.userId = userId;
    this.email = email;
    this.password = password;
  }

  // User 엔티티로부터 CustomUserDetails 생성
  // CustomUserDetailsService에서 DB 조회 후 사용
  public static CustomUserDetails from(User user) {
    return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword());
  }

  // 사용자 권한 반환 - 현재는 모든 사용자에게 ROLE_USER 권한 부여
  // SecurityConfig에서 .hasRole("USER") 체크할 때 사용
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
  }

  @Override
  public String getPassword() {
    return password;
  }

  // Spring Security에서 사용자 식별자로 사용 - 우리는 email을 식별자로 사용
  @Override
  public String getUsername() {
    return email;
  }

  // 계정 만료 여부 - true: 만료되지 않음
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  // 계정 잠금 여부 - true: 잠기지 않음
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  // 비밀번호 만료 여부 - true: 만료되지 않음
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  // 계정 활성화 여부 - true: 활성화됨
  @Override
  public boolean isEnabled() {
    return true;
  }
}