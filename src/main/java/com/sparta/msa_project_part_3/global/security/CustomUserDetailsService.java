package com.sparta.msa_project_part_3.global.security;

import com.sparta.msa_project_part_3.domain.user.entity.User;
import com.sparta.msa_project_part_3.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Security가 로그인 시 사용자 정보를 DB에서 조회할 때 사용
// authenticationManager.authenticate() 호출 시 내부적으로 이 서비스가 실행됨
// 즉 로그인 요청 → authenticationManager → loadUserByUsername → DB 조회 → 비밀번호 검증 순서
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  // Spring Security가 email(username)로 사용자 조회할 때 호출
  // 사용자가 없으면 UsernameNotFoundException 발생 → 로그인 실패 처리
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

    // 조회한 User 엔티티를 CustomUserDetails로 변환하여 반환
    return CustomUserDetails.from(user);
  }
}