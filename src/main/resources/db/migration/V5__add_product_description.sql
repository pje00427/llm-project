-- products 테이블에 description 컬럼 추가
ALTER TABLE products ADD COLUMN description TEXT;

-- 토너 상품 description 업데이트
UPDATE products SET description = '촉촉하고 크리미한 텍스처로 피부 장벽을 강화해주는 고기능성 토너. 건성 피부에 적합한 보습 토너.' WHERE name = '라네즈 크림 스킨 토너';
UPDATE products SET description = '제주 녹차 성분이 풍부하게 담긴 자연주의 토너. 피부 진정 및 산화 방지 효과.' WHERE name = '이니스프리 그린티 토너';
UPDATE products SET description = 'AHA와 BHA 성분으로 각질을 부드럽게 제거하고 모공을 정돈해주는 토너. 트러블성 피부에 적합.' WHERE name = '코스알엑스 AHA BHA 토너';
UPDATE products SET description = '갈락토미세스 발효 필트레이트 함유 피부 결을 개선하는 에센스 토너. 밝고 투명한 피부톤으로.' WHERE name = '미샤 타임레볼루션 토너';

-- 세럼 상품 description 업데이트
UPDATE products SET description = '피테라 성분의 프리미엄 에센스. 피부 자연 재생력을 높여 맑고 투명한 피부로 개선. 명품 스킨케어.' WHERE name = 'SK-II 페이셜 트리트먼트 에센스';
UPDATE products SET description = '달팽이 분비물 여과물 96.3% 함유 세럼. 피부 재생 및 보습, 상처 회복에 탁월한 효과.' WHERE name = '코스알엑스 달팽이 세럼';
UPDATE products SET description = '바이오셀룰로오스 기술의 프리미엄 세럼. 피부 속까지 깊은 보습과 탄력 개선 효과.' WHERE name = '아이오페 바이오 세럼';
UPDATE products SET description = '피부 고민에 맞춤형 복합 성분 집중 케어 세럼. 가성비 좋은 일상 피부관리 세럼.' WHERE name = '더페이스샵 더 원 세럼';

-- 크림 상품 description 업데이트
UPDATE products SET description = '수분을 피부 깊숙이 채워주는 워터뱅크 기술. 촉촉하고 가벼운 수분 크림으로 건조한 피부에 최적.' WHERE name = '라네즈 워터뱅크 크림';
UPDATE products SET description = '제주 화산송이 성분으로 피지 조절과 모공 관리에 탁월. 지성 피부와 복합성 피부에 적합한 크림.' WHERE name = '이니스프리 슈퍼 화산송이 크림';
UPDATE products SET description = '시카 성분 함유 민감하고 손상된 피부 진정에 특화. 피부 장벽 회복 및 자극 완화 집중 크림.' WHERE name = '닥터자르트 시카페어 크림';
UPDATE products SET description = '한방 성분의 순한 기초 크림. 민감한 피부도 편안하게 사용할 수 있는 자극 없는 보습 크림.' WHERE name = '한율 순 크림';

-- 선크림 상품 description 업데이트
UPDATE products SET description = '자작나무 수액 성분의 순한 선크림. 백탁 없이 가볍게 발리고 촉촉한 사용감. 민감 피부 적합.' WHERE name = '라운드랩 자작나무 선크림';
UPDATE products SET description = '가벼운 텍스처로 끈적임 없이 퍼지는 산뜻한 선크림. 일상 자외선 차단에 최적화된 제품.' WHERE name = '이니스프리 퍼펙트 선크림';
UPDATE products SET description = '피부 톤업 효과와 자외선 차단을 동시에. 화이트닝 케어 선크림으로 맑은 피부를 연출.' WHERE name = '넘버즈인 솔라 선크림';

-- 클렌징 상품 description 업데이트
UPDATE products SET description = '발리면 오일로 변하는 클렌징밤. 메이크업과 선크림을 부드럽게 녹여내는 딥클렌징 제품.' WHERE name = '바닐라코 클린잇 제로 클렌징밤';
UPDATE products SET description = '살리실산 성분의 저자극 클렌저. 모공 속 노폐물을 효과적으로 제거하고 트러블을 예방.' WHERE name = '코스알엑스 살리실산 클렌저';
UPDATE products SET description = '제주 녹차 성분의 부드러운 클렌저. 자극 없이 클렌징하며 세안 후 당김 없이 촉촉한 마무리.' WHERE name = '이니스프리 그린티 클렌저';

-- 마스크팩 상품 description 업데이트
UPDATE products SET description = '한 장에 앰플 한 병 NMF 수분 공급 마스크팩. 즉각적인 보습과 피부 진정 효과로 피부과 추천.' WHERE name = '메디힐 NMF 마스크팩 10매';
UPDATE products SET description = '골드와 콜라겐 성분의 안티에이징 마스크팩. 탄력 및 광채 개선으로 피부를 리프팅.' WHERE name = 'SNP 골드 콜라겐 마스크팩';
UPDATE products SET description = '꿀 성분의 광채 마스크팩. 칙칙한 피부톤을 환하게 밝혀주고 수분 공급으로 생기 있는 피부로.' WHERE name = 'JM솔루션 꿀광 마스크팩';
