package com.sparta.msa_project_part_3.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

// Redis를 HTTP 세션 저장소로 사용하도록 활성화
// 서버 재시작해도 세션 유지, 여러 서버 간 세션 공유 가능
@Configuration
@EnableRedisHttpSession
public class SessionConfig {

    // 세션 데이터를 Java 직렬화 방식으로 Redis에 저장
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return RedisSerializer.java();
    }
}