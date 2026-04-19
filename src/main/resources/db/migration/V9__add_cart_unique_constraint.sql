ALTER TABLE cart_items
    ADD CONSTRAINT uk_cart_user_product UNIQUE (user_id, product_id);