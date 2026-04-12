package com.sparta.msa_project_part_3.domain.product.service;

import com.sparta.msa_project_part_3.domain.product.dto.ProductSearchCondition;
import com.sparta.msa_project_part_3.domain.product.dto.response.RecommendedProduct;
import com.sparta.msa_project_part_3.domain.product.entity.Product;
import com.sparta.msa_project_part_3.global.exception.DomainException;
import com.sparta.msa_project_part_3.global.exception.DomainExceptionCode;
import com.sparta.msa_project_part_3.global.prompt.ProductPrompts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductLlmService {

    private final ChatClient chatClient;

    /**
     * 사용자의 자연어 검색어를 구조화된 검색 조건으로 파싱
     * 이전 검색 이력을 컨텍스트로 활용
     */
    public ProductSearchCondition parseSearchCondition(String query, String userId) {

        String sanitizedQuery = query
                .replaceAll("[\"']", "")
                .replaceAll("(?i)(ignore|forget|무시|위의|지시|system|prompt)", "");

        try {
            return chatClient.prompt()
                    .system(ProductPrompts.HISTORY_SYSTEM_PROMPT)
                    .user(sanitizedQuery)
                    .advisors(advisor -> advisor
                            .param("chat_memory_conversation_id", userId) // userId로 대화 구분
                    )
                    .call()
                    .entity(ProductSearchCondition.class);
        } catch (IllegalArgumentException e) {
            log.warn("부적절한 내용 감지: {}", e.getMessage());
            throw new DomainException(DomainExceptionCode.BLOCKED_CONTENT);
        }  catch (Exception e) {
            log.error("LLM 파싱 실패: {}", e.getMessage());
            throw new DomainException(DomainExceptionCode.LLM_PARSING_FAILED);
        }
    }
    /**
     * RAG용 LLM 조건 추출
     * 벡터 검색 후보 상품 목록을 컨텍스트로 활용
     */
    public ProductSearchCondition extractConditionWithCandidates(String query, List<Document> candidateDocuments) {

        StringBuilder candidateList = new StringBuilder("후보 상품 목록:\n");
        for (int i = 0; i < candidateDocuments.size(); i++) {
            Document doc = candidateDocuments.get(i);
            Object price = doc.getMetadata().get("price");
            candidateList.append(i + 1).append(". ")
                .append("내용: ").append(doc.getText())
                .append(", 가격: ").append(price).append("원")
                .append("\n");
        }

        String sanitizedQuery = query
            .replaceAll("[\"']", "")
            .replaceAll("(?i)(ignore|forget|무시|위의|지시|system|prompt)", "");

        String userPrompt = """
                %s
                요청: %s
                """.formatted(candidateList, sanitizedQuery);

        try {
            return chatClient.prompt()
                .system(ProductPrompts.RAG_SYSTEM_PROMPT)
                .user(userPrompt)
                .call()
                .entity(ProductSearchCondition.class);
        } catch (Exception e) {
            log.error("RAG LLM 파싱 실패: {}", e.getMessage());
            throw new DomainException(DomainExceptionCode.LLM_PARSING_FAILED);
        }
    }

    /**
     * 검색된 상품 목록 중 가장 적합한 상품 선정 + 추천 홍보 문구 생성
     */
    public RecommendedProduct generateRecommendation(String query, List<Product> products) {

        String productList = products.stream()
            .map(p -> "ID: %d, 이름: %s, 가격: %d원, 평점: %.1f"
                .formatted(p.getId(), p.getName(), p.getPrice(), p.getRating()))
            .reduce("", (a, b) -> a + "\n" + b);

        String prompt = """
                사용자 요청: %s
                
                검색된 상품 목록:
                %s
                
                위 상품 중 사용자 요청에 가장 적합한 상품 1개를 선정하고,
                productId: 선정된 상품 ID
                message: 감성적인 추천 홍보 문구
                를 반환해주세요.
                """.formatted(query, productList);

        try {
            return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(RecommendedProduct.class);
        } catch (Exception e) {
            log.error("LLM 추천 파싱 실패: {}", e.getMessage());
            throw new DomainException(DomainExceptionCode.LLM_PARSING_FAILED);
        }
    }
}