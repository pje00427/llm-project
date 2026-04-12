package com.sparta.msa_project_part_3.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.msa_project_part_3.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

// Spring Security 전체 설정을 담당하는 클래스
// 인증/인가 규칙, 세션 관리, 비밀번호 암호화 등을 설정
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  // 인증 없이 접근 가능한 URL 목록
  // 스웨거, 인증 API는 로그인 없이 접근 가능해야 하므로 제외
  public static final String[] SECURITY_EXCLUDE_PATHS = {
      "/public/**", "/api/swagger-ui/**", "/swagger-ui/**", "/swagger-ui.html",
      "/api/v3/api-docs/**", "/v3/api-docs/**", "/favicon.ico", "/actuator/**",
      "/swagger-resources/**", "/external/**", "/api/auth/**"
  };

  private final ObjectMapper objectMapper;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // CSRF 비활성화 - REST API는 세션 대신 토큰 방식이라 불필요
        .csrf(AbstractHttpConfigurer::disable)
        // 세션 기반 SecurityContext 저장소 설정
        // 로그인 후 인증 정보를 HttpSession에 저장
        .securityContext(context -> context
            .securityContextRepository(securityContextRepository())
        )
        // 세션 관리 설정
        // IF_REQUIRED: 필요할 때만 세션 생성
        // maximumSessions(1): 동시 로그인 1개로 제한
        // maxSessionsPreventsLogin(false): 새 로그인 시 기존 세션 만료 (false = 기존 세션 만료)
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .maximumSessions(1)
            .maxSessionsPreventsLogin(false)
        )
        // 인증/인가 규칙 설정
        // SECURITY_EXCLUDE_PATHS: 인증 없이 접근 가능
        // /api/**: ROLE_USER 권한 필요
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(SECURITY_EXCLUDE_PATHS).permitAll()
            .requestMatchers("/api/**").hasRole("USER")
            .anyRequest().authenticated()
        )
        // 폼 로그인, HTTP Basic 인증 비활성화
        // REST API는 JSON으로 로그인 처리하므로 불필요
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        // 인증 실패 시 401 응답 반환
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, authException) -> {
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.setContentType("application/json;charset=UTF-8");
              ApiResponse<Void> errorResponse = ApiResponse.<Void>builder()
                  .error(ApiResponse.Error.of("UNAUTHORIZED", "Authentication required"))
                  .build();
              response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            })
        );

    return http.build();
  }

  // 인증 정보를 HttpSession에 저장/조회하는 저장소
  // 로그인 시 SecurityContext를 세션에 저장
  // 이후 요청마다 세션에서 인증 정보를 꺼내서 사용
  @Bean
  public SecurityContextRepository securityContextRepository() {
    return new HttpSessionSecurityContextRepository();
  }

  // Spring Security 인증 처리를 담당하는 매니저
  // AuthService에서 로그인 시 authenticationManager.authenticate() 호출
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  // 비밀번호 암호화 Bean
  // 회원가입 시 비밀번호 암호화, 로그인 시 비밀번호 검증에 사용
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}