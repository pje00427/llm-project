package com.sparta.msa_project_part_3.global.config;

import com.sparta.msa_project_part_3.global.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatClientConfig {

  @Bean
  public ChatMemory chatMemory() {
    return MessageWindowChatMemory.builder()
            .chatMemoryRepository(new InMemoryChatMemoryRepository())
            .build();
  }

  @Bean
  public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
    return builder
            .defaultSystem("""
                            당신은 스킨케어 & 코스메틱 상품 전문 쇼핑 어드바이저입니다.
                            사용자의 요청에 맞는 상품을 친절하게 추천해주세요.
                            """)
            .defaultAdvisors(
                    new SafeGuardAdvisor(
                            List.of("욕설", "비속어", "꺼져", "씨발", "개새끼", "병신")
                    ),
                    MessageChatMemoryAdvisor.builder(chatMemory).build()
            )
            .build();
  }
}
