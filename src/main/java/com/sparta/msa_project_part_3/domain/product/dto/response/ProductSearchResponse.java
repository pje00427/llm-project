package com.sparta.msa_project_part_3.domain.product.dto.response;

import com.sparta.msa_project_part_3.global.response.PageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSearchResponse {
    private RecommendedProduct recommended;
    private PageResponse<ProductResponse> products;
}