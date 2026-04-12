package com.sparta.msa_project_part_3.domain.product.repository;

import com.sparta.msa_project_part_3.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

    Page<Product> searchProducts(String keyword, String category, Integer minPrice, Integer maxPrice, Pageable pageable);
}