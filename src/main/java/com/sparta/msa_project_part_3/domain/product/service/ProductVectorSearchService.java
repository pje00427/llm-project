package com.sparta.msa_project_part_3.domain.product.service;

import com.sparta.msa_project_part_3.global.exception.DomainException;
import com.sparta.msa_project_part_3.global.exception.DomainExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductVectorSearchService {

    private final VectorStore vectorStore;

    /**
     * 벡터 유사도 검색 - 캐시 적용
     * 동일한 query는 Gemini API 재호출 없이 캐시에서 반환
     */
    @Cacheable(value = "embeddings", key = "#query", cacheManager = "caffeineCacheManager")
    public List<Document> searchCandidates(String query) {
        log.info("캐시 미스 - Gemini API 호출: {}", query);
        try {
            return vectorStore.similaritySearch(
                    SearchRequest.builder().query(query).topK(50).build()
            );
        } catch (Exception e) {
            log.error("벡터 검색 실패: {}", e.getMessage());
            throw new DomainException(DomainExceptionCode.VECTOR_SEARCH_FAILED);
        }
    }
}