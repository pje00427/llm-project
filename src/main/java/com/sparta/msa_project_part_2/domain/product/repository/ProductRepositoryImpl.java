package com.sparta.msa_project_part_2.domain.product.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.msa_project_part_2.domain.product.entity.Product;
import com.sparta.msa_project_part_2.domain.product.entity.QProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QProduct product = QProduct.product;

    @Override
    public Page<Product> searchProducts(String keyword, String category, Integer minPrice, Integer maxPrice, Pageable pageable) {

        List<Product> content = queryFactory
                .selectFrom(product)
                .where(
                        keywordContains(keyword),
                        categoryContains(category),
                        priceGoe(minPrice),
                        priceLoe(maxPrice)
                )
                .orderBy(product.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(product.count())
                .from(product)
                .where(
                        keywordContains(keyword),
                        categoryContains(category),
                        priceGoe(minPrice),
                        priceLoe(maxPrice)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private BooleanExpression keywordContains(String keyword) {
        if (keyword == null) return null;

        // "토너, 크림, 세럼" → ["토너", "크림", "세럼"] OR 조건으로 검색
        String[] keywords = keyword.split(",");
        BooleanExpression result = null;
        for (String kw : keywords) {
            BooleanExpression expr = product.name.contains(kw.trim());
            result = result == null ? expr : result.or(expr);
        }
        return result;
    }

    private BooleanExpression categoryContains(String category) {
        return category != null ? product.category.name.contains(category) : null;
    }

    private BooleanExpression priceGoe(Integer minPrice) {
        return minPrice != null ? product.price.goe(minPrice) : null;
    }

    private BooleanExpression priceLoe(Integer maxPrice) {
        return maxPrice != null ? product.price.loe(maxPrice) : null;
    }
}