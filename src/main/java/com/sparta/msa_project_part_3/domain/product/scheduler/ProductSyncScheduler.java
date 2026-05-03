package com.sparta.msa_project_part_3.domain.product.scheduler;

import com.sparta.msa_project_part_3.domain.product.service.ProductExternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSyncScheduler {

  private final ProductExternalService productExternalService;

  @Scheduled(cron = "0 0 * * * *")
  public void syncExternalProducts() {
    log.info("외부 상품 동기화 스케줄러 실행");
    productExternalService.fetchAndSyncProducts();
  }
}