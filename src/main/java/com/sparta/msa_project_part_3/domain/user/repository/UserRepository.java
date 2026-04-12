package com.sparta.msa_project_part_3.domain.user.repository;

import com.sparta.msa_project_part_3.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// User 엔티티의 DB 접근을 담당하는 인터페이스
// JpaRepository를 상속받아 기본 CRUD 메서드 자동 제공
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  // 이메일로 사용자 조회 - 로그인 시 사용자 찾을 때 사용
  // 중복 이메일 체크할 때도 사용
  Optional<User> findByEmail(String email);
}