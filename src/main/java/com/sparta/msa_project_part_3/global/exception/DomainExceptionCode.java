package com.sparta.msa_project_part_3.global.exception;

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
    BLOCKED_CONTENT(HttpStatus.BAD_REQUEST, "부적절한 내용이 포함되어 있습니다."),
    // 사용자를 찾을 수 없을 때 (로그인 실패, 내 정보 조회 시 미로그인 등)
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    // 장바구니 아이템을 찾을 수 없을 때
    NOT_FOUND_CART_ITEM(HttpStatus.NOT_FOUND, "장바구니 상품을 찾을 수 없습니다."),
    // 이메일 중복 가입 시도 시
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    // 쿠폰을 찾을 수 없을 때
    NOT_FOUND_COUPON(HttpStatus.NOT_FOUND, "쿠폰을 찾을 수 없습니다."),
    // 유효하지 않은 쿠폰 코드일 때
    INVALID_COUPON_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 쿠폰 코드입니다."),
    // 쿠폰 사용 한도 초과 시
    COUPON_USAGE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "쿠폰 사용 한도를 초과했습니다."),
    // 이미 등록된 쿠폰일 때
    ALREADY_REGISTERED_COUPON(HttpStatus.CONFLICT, "이미 등록된 쿠폰입니다.");
    private final HttpStatus status;
    private final String message;
}