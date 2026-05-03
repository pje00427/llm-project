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
import org.springframework.ai.document.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductRagService {

    private final ProductVectorSearchService productVectorSearchService;
    private final ProductRepository productRepository;
    private final ProductLlmService productLlmService;

    @Transactional(readOnly = true)
    public ProductSearchResponse searchRag(String query, Pageable pageable) {
        // Step 1: 벡터 유사도 검색 (캐시 적용)
        List<Document> candidates = productVectorSearchService.searchCandidates(query);
        log.info("벡터 검색 완료 - 후보 수: {}", candidates.size());

        // Step 2: 후보 상품 컨텍스트로 LLM 검색 조건 추출
        // LLM 실패 시 빈 조건으로 폴백 (전체 상품 조회)
        ProductSearchCondition condition;
        try {
            condition = productLlmService.extractConditionWithCandidates(query, candidates);
        } catch (Exception e) {
            log.warn("LLM 조건 추출 실패 - 빈 조건으로 폴백: {}", e.getMessage());
            condition = new ProductSearchCondition(null, null, null, null);
        }
        log.info("RAG 파싱된 검색 조건 - keyword: {}, category: {}, minPrice: {}, maxPrice: {}",
                condition.getKeyword(), condition.getCategory(),
                condition.getMinPrice(), condition.getMaxPrice());

        // 상품 검색과 무관한 질문 예외처리
        if (condition.getKeyword() == null
                && condition.getCategory() == null
                && condition.getMinPrice() == null
                && condition.getMaxPrice() == null) {
            throw new DomainException(DomainExceptionCode.INVALID_SEARCH_QUERY);
        }

        // Step 3: QueryDSL 정밀 필터링
        Page<Product> products = productRepository.searchProducts(
                condition.getKeyword(),
                condition.getCategory(),
                condition.getMinPrice(),
                condition.getMaxPrice(),
                null,
                pageable
        );

        // 검색 결과 없으면 추천 생략
        if (products.isEmpty()) {
            return ProductSearchResponse.builder()
                    .recommended(null)
                    .products(new PageResponse<>(products.map(ProductResponse::from)))
                    .build();
        }

        // Step 4: LLM 추천 문구 생성
        // LLM 실패 시 기본 메시지로 폴백
        List<Product> productList = products.getContent();
        RecommendedProduct recommended;
        try {
            recommended = productLlmService.generateRecommendation(query, productList);
        } catch (Exception e) {
            log.warn("LLM 추천 생성 실패 - 기본 메시지로 폴백: {}", e.getMessage());
            recommended = RecommendedProduct.builder()
                    .productId(productList.get(0).getId())
                    .message("이런 상품은 어떠세요?")
                    .build();
        }

        // LLM 반환 productId 유효성 검증
        List<Long> validIds = productList.stream().map(Product::getId).toList();
        if (recommended.getProductId() == null || !validIds.contains(recommended.getProductId())) {
            log.warn("RAG LLM이 유효하지 않은 productId 반환: {} → 첫 번째 상품으로 대체", recommended.getProductId());
            recommended = RecommendedProduct.builder()
                    .productId(productList.get(0).getId())
                    .message(recommended.getMessage())
                    .build();
        }

        // Step 5: 응답 반환
        return ProductSearchResponse.builder()
                .recommended(recommended)
                .products(new PageResponse<>(products.map(ProductResponse::from)))
                .build();
    }
}