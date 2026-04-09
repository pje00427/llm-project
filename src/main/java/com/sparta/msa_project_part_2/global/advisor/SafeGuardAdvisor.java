package com.sparta.msa_project_part_2.global.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;

import java.util.List;

@Slf4j
public class SafeGuardAdvisor implements BaseAdvisor {

    private final List<String> blockedKeywords;

    public SafeGuardAdvisor(List<String> blockedKeywords) {
        this.blockedKeywords = blockedKeywords;
    }

    /**
     * 입력 검증 - LLM 호출 전 실행
     */
    @Override
    public AdvisedRequest before(AdvisedRequest request) {
        String userText = request.userText();

        for (String keyword : blockedKeywords) {
            if (userText != null && userText.contains(keyword)) {
                log.warn("부적절한 입력 감지 - 키워드: {}", keyword);
                throw new IllegalArgumentException("부적절한 내용이 포함되어 있습니다.");
            }
        }
        return request;
    }

    /**
     * 출력 검증 - LLM 응답 후 실행
     */
    @Override
    public AdvisedResponse after(AdvisedResponse response) {
        String responseText = response.response()
                .getResult()
                .getOutput()
                .getText();

        for (String keyword : blockedKeywords) {
            if (responseText != null && responseText.contains(keyword)) {
                log.warn("부적절한 응답 감지 - 키워드: {}", keyword);
                throw new IllegalArgumentException("부적절한 응답이 감지되었습니다.");
            }
        }
        return response;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}