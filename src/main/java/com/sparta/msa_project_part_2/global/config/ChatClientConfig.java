package com.sparta.msa_project_part_2.global.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

  @Bean
  public ChatClient chatClient(ChatClient.Builder builder) {
    return builder
        .defaultSystem("""
                        당신은 스킨케어 & 코스메틱 상품 전문 쇼핑 어드바이저입니다.
                        사용자의 요청에 맞는 상품을 친절하게 추천해주세요.
                        """)
        .build();
  }
}