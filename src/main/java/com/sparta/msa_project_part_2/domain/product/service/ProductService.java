package com.sparta.msa_project_part_2.domain.product.service;

import com.sparta.msa_project_part_2.domain.product.dto.ProductSearchCondition;
import com.sparta.msa_project_part_2.domain.product.dto.response.ProductSearchResponse;
import com.sparta.msa_project_part_2.domain.product.dto.response.RecommendedProduct;
import com.sparta.msa_project_part_2.domain.product.entity.Product;
import com.sparta.msa_project_part_2.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductLlmService productLlmService;

    public ProductSearchResponse search(String query) {

        // Step 1: 자연어 → 검색 조건 파싱
        ProductSearchCondition condition = productLlmService.parseSearchCondition(query);
        log.info("파싱된 검색 조건 - keyword: {}, category: {}, minPrice: {}, maxPrice: {}",
                condition.getKeyword(), condition.getCategory(),
                condition.getMinPrice(), condition.getMaxPrice());

        // Step 2: QueryDSL로 DB 검색
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Product> products = productRepository.searchProducts(
                condition.getKeyword(),
                condition.getCategory(),
                condition.getMinPrice(),
                condition.getMaxPrice(),
                pageable
        );

        // Step 3: LLM으로 추천 상품 선정 + 홍보 문구 생성
        List<Product> productList = products.getContent();
        RecommendedProduct recommended = productLlmService.generateRecommendation(query, productList);

        return ProductSearchResponse.builder()
                .recommended(recommended)
                .products(products)
                .build();
    }
}