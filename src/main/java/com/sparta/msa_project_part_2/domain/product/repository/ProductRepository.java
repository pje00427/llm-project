package com.sparta.msa_project_part_2.domain.product.repository;

import com.sparta.msa_project_part_2.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
}
