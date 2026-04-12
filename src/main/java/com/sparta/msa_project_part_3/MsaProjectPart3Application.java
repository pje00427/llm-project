package com.sparta.msa_project_part_3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// @EnableJpaAuditing: BaseEntity의 createdAt/updatedAt 자동 관리 활성화
@EnableJpaAuditing
@SpringBootApplication
public class MsaProjectPart3Application {
	public static void main(String[] args) {
		SpringApplication.run(MsaProjectPart3Application.class, args);
	}
}