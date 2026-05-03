package com.sparta.msa_project_part_3.domain.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponse {

  private List<ProductResponse> products;
  private Long total;
  private Integer page;
  private Integer pageSize;

  public static ProductListResponse of(Page<ProductResponse> productPage) {
    return ProductListResponse.builder()
        .products(productPage.getContent())
        .total(productPage.getTotalElements())
        .page(productPage.getNumber() + 1)
        .pageSize(productPage.getSize())
        .build();
  }
}