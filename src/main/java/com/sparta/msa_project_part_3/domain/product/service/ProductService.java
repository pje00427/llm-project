package com.sparta.msa_project_part_3.domain.product.service;

import com.sparta.msa_project_part_3.domain.product.dto.ProductSearchCondition;
import com.sparta.msa_project_part_3.global.response.PageResponse;
import com.sparta.msa_project_part_3.domain.product.dto.response.ProductResponse;
import com.sparta.msa_project_part_3.domain.product.dto.response.ProductSearchResponse;
import com.sparta.msa_project_part_3.domain.product.dto.response.RecommendedProduct;
import com.sparta.msa_project_part_3.domain.product.entity.Product;
import com.sparta.msa_project_part_3.domain.product.repository.ProductRepository;
import com.sparta.msa_project_part_3.global.exception.DomainException;
import com.sparta.msa_project_part_3.global.exception.DomainExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductLlmService productLlmService;

    @Transactional
    public ProductSearchResponse search(String query, String userId) {

        // Step 1: 자연어 → 검색 조건 파싱 (Advisor가 이력 자동 관리)
        ProductSearchCondition condition = productLlmService.parseSearchCondition(query, userId);
        log.info("파싱된 검색 조건 - keyword: {}, category: {}, minPrice: {}, maxPrice: {}",
                condition.getKeyword(), condition.getCategory(),
                condition.getMinPrice(), condition.getMaxPrice());

        // 상품 검색과 무관한 질문 예외처리
        if (condition.getKeyword() == null
                && condition.getCategory() == null
                && condition.getMinPrice() == null
                && condition.getMaxPrice() == null) {
            throw new DomainException(DomainExceptionCode.INVALID_SEARCH_QUERY);
        }

        // Step 2: QueryDSL로 DB 검색
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Product> products = productRepository.searchProducts(
                condition.getKeyword(),
                condition.getCategory(),
                condition.getMinPrice(),
                condition.getMaxPrice(),
                pageable
        );

        // 검색 결과가 없으면 추천 생략
        if (products.isEmpty()) {
            return ProductSearchResponse.builder()
                    .recommended(null)
                    .products(new PageResponse<>(products.map(ProductResponse::from)))
                    .build();
        }

        // Step 3: LLM으로 추천 상품 선정 + 홍보 문구 생성
        List<Product> productList = products.getContent();
        RecommendedProduct recommended = productLlmService.generateRecommendation(query, productList);

        // LLM이 반환한 productId 검증
        List<Long> validIds = productList.stream().map(Product::getId).toList();
        if (recommended.getProductId() == null || !validIds.contains(recommended.getProductId())) {
            log.warn("LLM이 유효하지 않은 productId 반환: {} → 첫 번째 상품으로 대체", recommended.getProductId());
            recommended = RecommendedProduct.builder()
                    .productId(productList.get(0).getId())
                    .message(recommended.getMessage())
                    .build();
        }

        return ProductSearchResponse.builder()
                .recommended(recommended)
                .products(new PageResponse<>(products.map(ProductResponse::from)))
                .build();
    }
}