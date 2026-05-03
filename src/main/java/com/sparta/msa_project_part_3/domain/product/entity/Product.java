package com.sparta.msa_project_part_3.domain.product.entity;

import com.sparta.msa_project_part_3.domain.category.entity.Category;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stock;

    private Double rating;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(unique = true)
    private String externalProductId;

    @Column(nullable = false)
    private Boolean isOrderable = true;

    @Column(nullable = false)
    private String productType = "INTERNAL";

    public static Product createExternal(String externalProductId, String name,
        String description, Integer price, Integer stock, Boolean isOrderable) {
        Product product = new Product();
        product.externalProductId = externalProductId;
        product.name = name;
        product.description = description;
        product.price = price;
        product.stock = stock;
        product.isOrderable = isOrderable;
        product.productType = "EXTERNAL";
        product.rating = 0.0;
        return product;
    }

    public void updateFromExternal(String name, String description,
        Integer price, Integer stock, Boolean isOrderable) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.isOrderable = isOrderable;
    }
}