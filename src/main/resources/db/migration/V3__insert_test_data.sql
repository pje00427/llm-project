-- 카테고리 삽입
INSERT INTO categories (name) VALUES ('토너');
INSERT INTO categories (name) VALUES ('세럼');
INSERT INTO categories (name) VALUES ('크림');
INSERT INTO categories (name) VALUES ('선크림');
INSERT INTO categories (name) VALUES ('클렌징');
INSERT INTO categories (name) VALUES ('마스크팩');

-- 토너 상품
INSERT INTO products (name, price, stock, rating, category_id, created_at, updated_at)
VALUES ('라네즈 크림 스킨 토너', 28000, 100, 4.8, 1, now(), now());

INSERT INTO products (name, price, stock, rating, category_id, created_at, updated_at)
VALUES ('이니스프리 그린티 토너', 18000, 150, 4.5, 1, now(), now());

INSERT INTO products (name, price, stock, rating, category_id, created_at, updated_at)
VALUES ('코스알엑스 AHA BHA 토너', 22000, 80, 4.7, 1, now(), now());

INSERT INTO products (name, price, stock, rating, category_id, created_at, updated_at)
VALUES ('미샤 타임레볼루션 토너', 15000, 200, 4.3, 1, now(), now());

-- 세럼 상품
INSERT INTO products (name, price, stock, rating, category_id, created_at, updated_at)
VALUES ('SK-II 페이셜 트리트먼트 에센스', 180000, 30, 4.9, 2, now(), now());

INSERT INTO products (name, price, stock, rating, category_id, created_at, updated_at)
VALUES ('코스알엑스 달팽이 세럼', 25000, 120, 4.8, 2, now(), now());

INSERT INTO products (name, price, stock, rating, category_id, created_at, updated_at)
VALUES ('아이오페 바이오 세럼', 65000, 50, 4.6, 2, now(), now());

INSERT INTO products (name, price, stock, rating, category_id, created_at, updated_at)
VALUES ('더페이스샵 더 원 세럼', 19000, 90, 4.2, 2, now(), now());

-- 크림 상품
INSERT INTO products (name, price, stock, rating, category_id, created_at, updated_at)
VALUES ('라네즈 워터뱅크 크림', 38000, 70, 4.7, 3, now(), now());

INSERT INTO products (name, price, stock, rating, category_id, created_at, updated_at)
VALUES ('이니스프리 슈퍼 화산송이 크림', 23000, 110, 4.5, 3, now(), now());

INSERT INTO products (name, price, stock, rating, category_id, created_at, updated_at)
VALUES ('닥터자르트 시카페어 크림', 32000, 85, 4.8, 3, now(), now());

INSERT INTO products (name, price, stock, rating, category_id, created_at, updated_at)
VALUES ('한율 순 크림', 17000, 130, 4.4, 3, now(), now());

-- 선크림 상품
INSERT INTO products (name, price, stock, rating, category_id, created_at, updated_at)
VALUES ('라운드랩 자작나무 선크림', 15000, 200, 4.9, 4, now(), now());

INSERT INTO products (name, price, stock, rating, category_id, created_at, updated_at)
VALUES ('이니스프리 퍼펙트 선크림', 18000, 150, 4.6, 4, now(), now());

INSERT INTO products (name, price, stock, rating, category_id, created_at, updated_at)
VALUES ('넘버즈인 솔라 선크림', 22000, 90, 4.7, 4, now(), now());

-- 클렌징 상품
INSERT INTO products (name, price, stock, rating, category_id, created_at, updated_at)
VALUES ('바닐라코 클린잇 제로 클렌징밤', 18000, 160, 4.8, 5, now(), now());

INSERT INTO products (name, price, stock, rating, category_id, created_at, updated_at)
VALUES ('코스알엑스 살리실산 클렌저', 14000, 140, 4.6, 5, now(), now());

INSERT INTO products (name, price, stock, rating, category_id, created_at, updated_at)
VALUES ('이니스프리 그린티 클렌저', 12000, 180, 4.4, 5, now(), now());

-- 마스크팩 상품
INSERT INTO products (name, price, stock, rating, category_id, created_at, updated_at)
VALUES ('메디힐 NMF 마스크팩 10매', 15000, 250, 4.7, 6, now(), now());

INSERT INTO products (name, price, stock, rating, category_id, created_at, updated_at)
VALUES ('SNP 골드 콜라겐 마스크팩', 25000, 120, 4.5, 6, now(), now());

INSERT INTO products (name, price, stock, rating, category_id, created_at, updated_at)
VALUES ('JM솔루션 꿀광 마스크팩', 12000, 300, 4.6, 6, now(), now());