package com.sparta.msa_project_part_3.domain.product.dto.response;

import com.sparta.msa_project_part_3.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private Integer price;
    private Integer stock;
    private Double rating;
    private String categoryName;

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .rating(product.getRating())
                .categoryName(product.getCategory() != null
                        ? product.getCategory().getName()
                        : null)
                .build();
    }
}