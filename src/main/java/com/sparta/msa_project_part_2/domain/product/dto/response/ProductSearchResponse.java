package com.sparta.msa_project_part_2.domain.product.dto.response;

import com.sparta.msa_project_part_2.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSearchResponse {
    private RecommendedProduct recommended;
    private Page<ProductResponse> products;
}