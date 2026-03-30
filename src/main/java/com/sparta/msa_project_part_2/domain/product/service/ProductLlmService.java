package com.sparta.msa_project_part_2.domain.product.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.msa_project_part_2.domain.product.dto.ProductSearchCondition;
import com.sparta.msa_project_part_2.domain.product.dto.response.RecommendedProduct;
import com.sparta.msa_project_part_2.domain.product.entity.Product;
import com.sparta.msa_project_part_2.global.exception.DomainException;
import com.sparta.msa_project_part_2.global.exception.DomainExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service

public class ProductLlmService {

    private final ChatClient chatClient;   // LLM 호출 도구
    private final ObjectMapper objectMapper; // JSON 변환 도구

    // @RequiredArgsConstructor 대신 직접 생성자 작성
    public ProductLlmService(ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    // ✅ Step 1: 자연어 → JSON → 객체 변환
    public ProductSearchCondition parseSearchCondition(String query) {

        // 1. LLM한테 보낼 프롬프트 작성
        String prompt = """
                사용자의 상품 검색 요청을 분석하여 JSON 형식으로 반환해주세요.
                
                요청: %s
                
                반드시 아래 JSON 형식으로만 응답하세요. 다른 설명은 절대 추가하지 마세요.
                {
                    "keyword": "검색 키워드 (없으면 null)",
                    "category": "카테고리명 (없으면 null)",
                    "minPrice": 최소가격 숫자 (없으면 null),
                    "maxPrice": 최대가격 숫자 (없으면 null)
                }
                """.formatted(query);

        // 2. LLM 호출 → 문자열(JSON) 응답 받기
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        // response = {"keyword":"랜턴","category":"캠핑 용품","minPrice":30000,"maxPrice":40000}

        // 3. JSON 문자열 → ProductSearchCondition 객체로 변환
        try {
            return objectMapper.readValue(response, ProductSearchCondition.class);
        } catch (Exception e) {
            log.error("LLM 응답 파싱 실패: {}", response);
            throw new DomainException(DomainExceptionCode.LLM_PARSING_FAILED);
        }
    }

    // ✅ Step 3: 검색 결과 → 추천 상품 선정 + 홍보 문구 생성
    public RecommendedProduct generateRecommendation(String query, List<Product> products) {

        // 1. 상품 목록을 문자열로 변환해서 LLM한테 전달
        String productList = products.stream()
                .map(p -> "ID: %d, 이름: %s, 가격: %d원, 평점: %.1f"
                        .formatted(p.getId(), p.getName(), p.getPrice(), p.getRating()))
                .reduce("", (a, b) -> a + "\n" + b);

        // 2. LLM한테 보낼 프롬프트 작성
        String prompt = """
                사용자 요청: %s
                
                검색된 상품 목록:
                %s
                
                위 상품 중 사용자 요청에 가장 적합한 상품 1개를 선정하고,
                반드시 아래 JSON 형식으로만 응답하세요. 다른 설명은 절대 추가하지 마세요.
                {
                    "productId": 선정된 상품 ID,
                    "message": "감성적인 추천 홍보 문구"
                }
                """.formatted(query, productList);

        // 3. LLM 호출 → 문자열(JSON) 응답 받기
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        // 4. JSON 문자열 → RecommendedProduct 객체로 변환
        try {
            return objectMapper.readValue(response, RecommendedProduct.class);
        } catch (Exception e) {
            log.error("LLM 추천 파싱 실패: {}", response);
            throw new DomainException(DomainExceptionCode.LLM_PARSING_FAILED);
        }
    }
}