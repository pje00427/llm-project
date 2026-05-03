package com.sparta.msa_project_part_3.domain.product.repository;

import com.sparta.msa_project_part_3.domain.product.entity.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    @Query("SELECT p FROM Product p JOIN FETCH p.category")
    List<Product> findAllWithCategory();

    // 외부 상품 조회
    Optional<Product> findByExternalProductId(String externalProductId);
}

