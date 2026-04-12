package com.sparta.msa_project_part_3.domain.category.repository;

import com.sparta.msa_project_part_3.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
