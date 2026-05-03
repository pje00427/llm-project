package com.sparta.msa_project_part_3.domain.product.client;

import com.sparta.msa_project_part_3.domain.product.dto.response.ExternalProductResponse;
import com.sparta.msa_project_part_3.global.config.OpenFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "external-product",
    url = "${external.shop.url}",
    configuration = OpenFeignConfig.class
)
public interface ExternalShopClient {

  @GetMapping("/products")
  ExternalProductResponse getProducts(
      @RequestParam("page") Integer page,
      @RequestParam("size") Integer size
  );
}