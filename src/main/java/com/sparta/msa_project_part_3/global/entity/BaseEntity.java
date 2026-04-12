package com.sparta.msa_project_part_3.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// 모든 엔티티에서 공통으로 사용하는 생성일시/수정일시 자동 관리
// @MappedSuperclass: 이 클래스 자체는 테이블이 없고 상속받는 엔티티에 컬럼으로 포함됨
// @EntityListeners: JPA Auditing 기능으로 생성/수정 시간 자동 입력
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

  // 최초 저장 시 자동으로 현재 시간 입력, 이후 변경 불가
  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  // 저장/수정 시마다 자동으로 현재 시간 업데이트
  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime updatedAt;
}