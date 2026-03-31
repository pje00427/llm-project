package com.sparta.msa_project_part_2.domain.product.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.msa_project_part_2.domain.ai.entity.SearchHistory;              // ✅ 추가
import com.sparta.msa_project_part_2.domain.ai.repository.SearchHistoryRepository; // ✅ 추가
import com.sparta.msa_project_part_2.domain.product.dto.ProductSearchCondition;
import com.sparta.msa_project_part_2.domain.product.dto.response.PageResponse;
import com.sparta.msa_project_part_2.domain.product.dto.response.ProductResponse;
import com.sparta.msa_project_part_2.domain.product.dto.response.ProductSearchResponse;
import com.sparta.msa_project_part_2.domain.product.dto.response.RecommendedProduct;
import com.sparta.msa_project_part_2.domain.product.entity.Product;
import com.sparta.msa_project_part_2.domain.product.repository.ProductRepository;
import com.sparta.msa_project_part_2.global.exception.DomainException;
import com.sparta.msa_project_part_2.global.exception.DomainExceptionCode;
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
    private final SearchHistoryRepository searchHistoryRepository;
    private final ObjectMapper objectMapper;

    public ProductSearchResponse search(String query, String userId) {

        // 추가 - 이전 검색 이력 조회 (userId가 있을 때만)
        List<SearchHistory> histories = List.of();
        if (userId != null && !userId.isBlank()) {
            histories = searchHistoryRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);
        }

        // Step 1: 자연어 → 검색 조건 파싱 (이력 포함)
        //  수정 - histories 파라미터 추가
        ProductSearchCondition condition = productLlmService.parseSearchCondition(query, histories);
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

        //  추가 - 검색 이력 저장 (userId가 있을 때만)
        if (userId != null && !userId.isBlank()) {
            try {
                String parsedJson = objectMapper.writeValueAsString(condition);
                searchHistoryRepository.save(
                    SearchHistory.builder()
                        .userId(userId)
                        .rawQuery(query)
                        .parsedCondition(parsedJson)
                        .build()
                );
            } catch (Exception e) {
                log.error("검색 이력 저장 실패: {}", e.getMessage());
            }
        }

        Page<ProductResponse> productResponses = products.map(ProductResponse::from);

        return ProductSearchResponse.builder()
            .recommended(recommended)
                .products(new PageResponse<>(productResponses))
                .build();
    }
}