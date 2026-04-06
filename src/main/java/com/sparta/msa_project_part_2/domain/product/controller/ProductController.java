package com.sparta.msa_project_part_2.domain.product.controller;

import com.sparta.msa_project_part_2.domain.product.dto.response.ProductSearchResponse;
import com.sparta.msa_project_part_2.domain.product.service.ProductRagService;
import com.sparta.msa_project_part_2.domain.product.service.ProductService;
import com.sparta.msa_project_part_2.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductRagService productRagService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<ProductSearchResponse>> search(
        @RequestParam String query,
        @RequestParam(required = false) String userId) {

        ProductSearchResponse response = productService.search(query, userId);
        return ApiResponse.ok(response);
    }

    @GetMapping("/search/rag")
    public ResponseEntity<ApiResponse<ProductSearchResponse>> searchRag(
        @RequestParam String query,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        ProductSearchResponse response = productRagService.searchRag(query, pageable);
        return ApiResponse.ok(response);
    }
}