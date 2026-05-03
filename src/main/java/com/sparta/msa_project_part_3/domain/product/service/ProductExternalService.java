package com.sparta.msa_project_part_3.domain.product.service;

import com.sparta.msa_project_part_3.domain.product.client.ExternalShopClient;
import com.sparta.msa_project_part_3.domain.product.dto.response.ExternalProductResponse;
import com.sparta.msa_project_part_3.domain.product.dto.response.ExternalProductResponse.ExternalItem;
import com.sparta.msa_project_part_3.domain.product.dto.response.ExternalProductResponse.ExternalPageable;
import com.sparta.msa_project_part_3.domain.product.entity.Product;
import com.sparta.msa_project_part_3.domain.product.repository.ProductRepository;
import com.sparta.msa_project_part_3.global.exception.DomainException;
import com.sparta.msa_project_part_3.global.exception.DomainExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductExternalService {

  private final ExternalShopClient externalShopClient;
  private final ProductRepository productRepository;

  @Transactional
  @Retryable(
      value = {ResourceAccessException.class, HttpServerErrorException.class, DomainException.class},
      maxAttempts = 4,
      backoff = @Backoff(delay = 1000, multiplier = 2)
  )
  public void fetchAndSyncProducts() {
    log.info("외부 상품 동기화 시작");

    int page = 0;
    int pageSize = 10;
    boolean lastPage = false;

    while (!lastPage) {
      ExternalProductResponse response = externalShopClient.getProducts(page, pageSize);

      // result=false 이면 externelShop이 고의 에러 던진 것 → Retry 유도
      if (response == null || Boolean.FALSE.equals(response.getResult())) {
        log.warn("외부 API 응답 실패 (page: {}), 재시도 예정", page);
        throw new DomainException(DomainExceptionCode.NOT_FOUND_PRODUCT);
      }

      if (response.getMessage() == null || response.getMessage().getContents() == null) {
        log.warn("외부 API 응답 데이터 없음 (page: {})", page);
        break;
      }

      List<ExternalItem> contents = response.getMessage().getContents();

      if (contents.isEmpty()) {
        break;
      }

      for (ExternalItem item : contents) {
        syncProduct(item);
      }

      ExternalPageable pageable = response.getMessage().getPageable();
      if (pageable != null) {
        lastPage = pageable.isLast();
      } else {
        lastPage = contents.size() < pageSize;
      }
      page++;
    }

    log.info("외부 상품 동기화 완료");
  }

  private void syncProduct(ExternalItem item) {
    String externalProductId = String.valueOf(item.getId());
    boolean isOrderable = item.getStock() != null && item.getStock() > 0;
    int price = item.getPrice() != null ? item.getPrice().intValue() : 0;

    Optional<Product> existing = productRepository.findByExternalProductId(externalProductId);

    if (existing.isPresent()) {
      existing.get().updateFromExternal(
          item.getName(),
          item.getDescription(),
          price,
          item.getStock(),
          isOrderable
      );
    } else {
      Product product = Product.createExternal(
          externalProductId,
          item.getName(),
          item.getDescription(),
          price,
          item.getStock(),
          isOrderable
      );
      productRepository.save(product);
    }
  }
}