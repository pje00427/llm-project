package com.sparta.msa_project_part_3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

// @EnableJpaAuditing: BaseEntity의 createdAt/updatedAt 자동 관리 활성화
@EnableJpaAuditing
@EnableRetry         // 추가
@EnableScheduling    // 추가
@EnableFeignClients  // 추가
@SpringBootApplication
public class MsaProjectPart3Application {
	public static void main(String[] args) {
		SpringApplication.run(MsaProjectPart3Application.class, args);
	}
}