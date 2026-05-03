package com.sparta.msa_project_part_3.domain.product.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ExternalProductResponse {

  private Boolean result;
  private ExternalError error;
  private ExternalPage message;

  @Getter
  @NoArgsConstructor
  public static class ExternalError {
    private String errorCode;
    private String errorMessage;
  }

  @Getter
  @NoArgsConstructor
  public static class ExternalPage {
    private List<ExternalItem> contents;
    private ExternalPageable pageable;
  }

  @Getter
  @NoArgsConstructor
  public static class ExternalItem {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
  }

  @Getter
  @NoArgsConstructor
  public static class ExternalPageable {
    private Long offset;
    private Long pageNumber;
    private Long pageSize;
    private Long pageElements;
    private Long totalPages;
    private Long totalElements;
    private boolean first;
    private boolean last;
  }
}