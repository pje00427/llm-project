package com.sparta.msa_project_part_2.domain.product.service;

import com.sparta.msa_project_part_2.domain.product.entity.Product;
import com.sparta.msa_project_part_2.domain.product.repository.ProductRepository;
import com.sparta.msa_project_part_2.global.exception.DomainException;
import com.sparta.msa_project_part_2.global.exception.DomainExceptionCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductEmbeddingService {

    private final VectorStore vectorStore;
    private final ProductRepository productRepository;

    public void embedProduct(Product product) {
        try {
            String content = buildContent(product);
            Map<String, Object> metadata = Map.of(
                "product_id", product.getId(),
                "category_id", product.getCategory() != null ? product.getCategory().getId() : 0L,
                "price", product.getPrice()
            );
            Document document = new Document(content, metadata);
            vectorStore.add(List.of(document));
            log.debug("상품 임베딩 완료: id={}", product.getId());
        } catch (Exception e) {
            log.error("상품 임베딩 실패: id={}, error={}", product.getId(), e.getMessage());
            throw new DomainException(DomainExceptionCode.EMBEDDING_FAILED);
        }
    }
    @Transactional
    public void embedAllProducts() {
        List<Product> products = productRepository.findAllWithCategory();
        log.info("전체 상품 임베딩 시작: {}개", products.size());

        List<Document> documents = products.stream()
            .map(product -> {
                String content = buildContent(product);
                Map<String, Object> metadata = Map.of(
                    "product_id", product.getId(),
                    "category_id", product.getCategory() != null ? product.getCategory().getId() : 0L,
                    "price", product.getPrice()
                );
                return new Document(content, metadata);
            })
            .toList();

        try {
            vectorStore.add(documents);
            log.info("전체 상품 임베딩 완료: {}개", documents.size());
        } catch (Exception e) {
            log.error("전체 상품 임베딩 실패: {}", e.getMessage());
            throw new DomainException(DomainExceptionCode.EMBEDDING_FAILED);
        }
    }

    private String buildContent(Product product) {
        String categoryName = product.getCategory() != null ? product.getCategory().getName() : "";
        if (product.getDescription() != null && !product.getDescription().isBlank()) {
            return product.getName() + " " + product.getDescription() + " " + categoryName;
        }
        return product.getName() + " " + categoryName;
    }
}
