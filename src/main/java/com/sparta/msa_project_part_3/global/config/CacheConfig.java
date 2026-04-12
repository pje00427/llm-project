package com.sparta.msa_project_part_3.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 쿼리 임베딩 캐시 설정
     * 동일한 검색어에 대해 Gemini API 재호출 없이 캐시에서 벡터 반환
     * - expireAfterWrite: 1시간 후 캐시 만료
     * - maximumSize: 최대 1000개 캐시 저장
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("embeddings");
        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(1000)
                .recordStats());
        return manager;
    }
}