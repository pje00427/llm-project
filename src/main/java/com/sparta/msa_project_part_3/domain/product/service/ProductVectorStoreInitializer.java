package com.sparta.msa_project_part_3.domain.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductVectorStoreInitializer implements ApplicationRunner {

    private final ProductEmbeddingService productEmbeddingService;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM vector_store", Integer.class
            );
            if (count == null || count == 0) {
                log.info("VectorStore가 비어있습니다. 전체 상품 임베딩을 시작합니다.");
                productEmbeddingService.embedAllProducts();
            } else {
                log.info("VectorStore에 이미 데이터가 있습니다. 건너뜁니다.");
            }
        } catch (Exception e) {
            log.error("VectorStore 초기화 중 오류 발생: {}", e.getMessage());
        }
    }
}