package com.sparta.msa_project_part_3.domain.ai.repository;

import com.sparta.msa_project_part_3.domain.ai.entity.SearchHistory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

  // 특정 유저의 최근 검색 이력 N건 조회 (최신순)
  List<SearchHistory> findTop5ByUserIdOrderByCreatedAtDesc(String userId);


  // 유저의 전체 이력 수 조회
  long countByUserId(String userId);

  // 유저의 가장 오래된 이력 1건 조회
  Optional<SearchHistory> findTopByUserIdOrderByCreatedAtAsc(String userId);
}