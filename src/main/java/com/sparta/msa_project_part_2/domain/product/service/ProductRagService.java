package com.sparta.msa_project_part_2.domain.product.service;

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
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductRagService {

    private final VectorStore vectorStore;
    private final ProductRepository productRepository;
    private final ProductLlmService productLlmService;

    @Transactional(readOnly = true)
    public ProductSearchResponse searchRag(String query, Pageable pageable) {
        // Step 1: 벡터 유사도 검색으로 후보 상품 50개 추출
        List<Document> candidates;
        try {
            candidates = vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(50).build()
            );
            log.info("벡터 검색 완료 - 후보 수: {}", candidates.size());
        } catch (Exception e) {
            log.error("벡터 검색 실패: {}", e.getMessage());
            throw new DomainException(DomainExceptionCode.VECTOR_SEARCH_FAILED);
        }

        // Step 2: 후보 상품 컨텍스트로 LLM 검색 조건 추출
        ProductSearchCondition condition = productLlmService.extractConditionWithCandidates(query, candidates);
        log.info("RAG 파싱된 검색 조건 - keyword: {}, category: {}, minPrice: {}, maxPrice: {}",
            condition.getKeyword(), condition.getCategory(),
            condition.getMinPrice(), condition.getMaxPrice());

        // Step 3: QueryDSL 정밀 필터링
        Page<Product> products = productRepository.searchProducts(
            condition.getKeyword(),
            condition.getCategory(),
            condition.getMinPrice(),
            condition.getMaxPrice(),
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
        List<Product> productList = products.getContent();
        RecommendedProduct recommended = productLlmService.generateRecommendation(query, productList);

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
