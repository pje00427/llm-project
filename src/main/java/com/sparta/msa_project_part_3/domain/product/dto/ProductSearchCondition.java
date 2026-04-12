package com.sparta.msa_project_part_3.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchCondition {
    private String keyword;
    private String category;
    private Integer minPrice;
    private Integer maxPrice;
}