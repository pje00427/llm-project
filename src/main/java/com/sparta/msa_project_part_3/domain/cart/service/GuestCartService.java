package com.sparta.msa_project_part_3.domain.cart.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.msa_project_part_3.domain.cart.dto.response.CartResponse;
import com.sparta.msa_project_part_3.domain.product.entity.Product;
import com.sparta.msa_project_part_3.domain.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuestCartService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ProductRepository productRepository;
    // @RequiredArgsConstructor 대상에서 제외하기 위해 final 없이 직접 생성
    // GenericJackson2JsonRedisSerializer 영향을 받지 않는 순수 ObjectMapper 사용
    private final ObjectMapper simpleMapper = new ObjectMapper();

    // 비회원 장바구니 Redis 키 생성
    // guest:cart:{세션ID} 형태로 저장
    private String getCartKey(String sessionId) {
        return "guest:cart:" + sessionId;
    }

    // 비회원 장바구니 조회 (상품 정보 조인 포함)
    public List<CartResponse> getCartItems(String sessionId) {
        String key = getCartKey(sessionId);
        String json = redisTemplate.opsForValue().get(key);

        if (json == null) {
            return new ArrayList<>();
        }

        try {
            List<CartResponse> items = simpleMapper.readValue(json,
                    new TypeReference<List<CartResponse>>() {});

            // 상품 정보 조인
            return items.stream()
                    .map(item -> {
                        Product product = productRepository.findById(item.getProductId())
                                .orElse(null);
                        return CartResponse.builder()
                                .productId(item.getProductId())
                                .quantity(item.getQuantity())
                                .productName(product != null ? product.getName() : null)
                                .productPrice(product != null ? product.getPrice() : null)
                                .build();
                    })
                    .toList();
        } catch (Exception e) {
            log.error("비회원 장바구니 조회 실패: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    // 비회원 장바구니 상품 추가
    // 이미 있는 상품이면 수량 증가, 없으면 새로 추가
    // TTL 7일 설정 - 7일 후 자동 삭제
    public List<CartResponse> addCartItem(String sessionId, Long productId, Integer quantity) {
        String key = getCartKey(sessionId);
        List<CartResponse> items = getCartItemsRaw(sessionId);

        boolean exists = false;
        for (CartResponse item : items) {
            if (item.getProductId().equals(productId)) {
                // 이미 담긴 상품 → 수량 증가
                items.set(items.indexOf(item), CartResponse.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity() + quantity)
                        .build());
                exists = true;
                break;
            }
        }

        if (!exists) {
            // 새 상품 → 추가
            items.add(CartResponse.builder()
                    .productId(productId)
                    .quantity(quantity)
                    .build());
        }

        saveToRedis(key, items);
        return getCartItems(sessionId);
    }

    // 비회원 장바구니 수량 수정
    public List<CartResponse> updateCartItem(String sessionId, Long productId, Integer quantity) {
        String key = getCartKey(sessionId);
        List<CartResponse> items = getCartItemsRaw(sessionId);

        items = items.stream()
                .map(item -> item.getProductId().equals(productId)
                        ? CartResponse.builder()
                        .productId(item.getProductId())
                        .quantity(quantity)
                        .build()
                        : item)
                .toList();

        saveToRedis(key, items);
        return getCartItems(sessionId);
    }

    // 비회원 장바구니 상품 삭제
    public void deleteCartItem(String sessionId, Long productId) {
        String key = getCartKey(sessionId);
        List<CartResponse> items = getCartItemsRaw(sessionId);

        items = items.stream()
                .filter(item -> !item.getProductId().equals(productId))
                .toList();

        saveToRedis(key, items);
    }

    // 비회원 장바구니 전체 삭제 - 로그인 후 합치기 완료 시 호출
    public void clearCart(String sessionId) {
        redisTemplate.delete(getCartKey(sessionId));
    }

    // 로그인 시 회원 장바구니로 합치기 위해 raw 데이터 반환 (상품 정보 조인 없음)
    public List<CartResponse> getCartItemsRaw(String sessionId) {
        String key = getCartKey(sessionId);
        String json = redisTemplate.opsForValue().get(key);

        if (json == null) {
            return new ArrayList<>();
        }

        try {
            return simpleMapper.readValue(json,
                    new TypeReference<List<CartResponse>>() {});
        } catch (Exception e) {
            log.error("비회원 장바구니 raw 조회 실패: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    // Redis에 장바구니 저장 (TTL 7일)
    private void saveToRedis(String key, List<CartResponse> items) {
        try {
            String json = simpleMapper.writeValueAsString(items);
            redisTemplate.opsForValue().set(key, json, 7, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("비회원 장바구니 저장 실패: {}", e.getMessage());
        }
    }
}