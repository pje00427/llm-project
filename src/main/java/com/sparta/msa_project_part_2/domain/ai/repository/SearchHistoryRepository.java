package com.sparta.msa_project_part_2.domain.ai.repository;

import com.sparta.msa_project_part_2.domain.ai.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

  // 특정 유저의 최근 검색 이력 N건 조회 (최신순)
  List<SearchHistory> findTop5ByUserIdOrderByCreatedAtDesc(String userId);
}