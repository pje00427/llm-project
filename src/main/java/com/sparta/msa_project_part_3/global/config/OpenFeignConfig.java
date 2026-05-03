package com.sparta.msa_project_part_3.global.config;

import feign.Retryer;
import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class OpenFeignConfig {

  @Bean
  public Request.Options feignOptions() {
    return new Request.Options(10000, TimeUnit.MILLISECONDS, 60000, TimeUnit.MILLISECONDS, true);
  }

  @Bean
  public Retryer feignRetryer() {
    return Retryer.NEVER_RETRY;
  }
}