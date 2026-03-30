package com.sparta.msa_project_part_2.domain.product.service;

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
@RequiredArgsConstructor
public class ProductLlmService {

    private final ChatClient chatClient;


    // Step 1: 자연어 → 객체로 자동 변환
    public ProductSearchCondition parseSearchCondition(String query) {
        String prompt = """
            사용자의 상품 검색 요청을 분석하여 검색 조건을 추출해주세요.
            
            요청: %s
            
            keyword: 상품 이름에서 찾을 수 있는 핵심 단어만 추출하세요. (예: 토너, 크림, 세럼)
                     촉촉한, 가성비, 추천 같은 형용사나 수식어는 포함하지 마세요.
                     keyword가 없으면 null로 설정하세요.
            category: 카테고리명 (없으면 null)
            minPrice: 최소가격 숫자 (없으면 null)
                      예: "2만원대" → minPrice: 20000
            maxPrice: 최대가격 숫자 (없으면 null)
                      예: "2만원대" → maxPrice: 29999
            
            만약 요청이 상품 검색과 전혀 무관한 내용이라면 (날씨, 음식, 일상 대화 등)
            모든 필드를 null로 반환하세요.
            """.formatted(query);

        try {
            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .entity(ProductSearchCondition.class);
        } catch (Exception e) {
            log.error("LLM 파싱 실패: {}", e.getMessage());
            throw new DomainException(DomainExceptionCode.LLM_PARSING_FAILED);
        }
    }
    // Step 3: 추천 상품 선정 + 홍보 문구 생성
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