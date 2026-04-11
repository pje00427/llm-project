package com.sparta.msa_project_part_2.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DomainExceptionCode {

    NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    INVALID_SEARCH_QUERY(HttpStatus.BAD_REQUEST, "상품 검색과 무관한 요청입니다."),
    LLM_PARSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "LLM 응답 파싱에 실패했습니다."),
    EMBEDDING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "임베딩 처리에 실패했습니다."),
    VECTOR_SEARCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "벡터 검색에 실패했습니다."),
    BLOCKED_CONTENT(HttpStatus.BAD_REQUEST, "부적절한 내용이 포함되어 있습니다.");
    private final HttpStatus status;
    private final String message;
}