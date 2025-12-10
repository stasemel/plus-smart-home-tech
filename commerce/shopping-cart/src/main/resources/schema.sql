CREATE TABLE IF NOT EXISTS shopping_carts
(
    id        UUID PRIMARY KEY,
    user_name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS shopping_cart_items
(
    cart_id    UUID    NOT NULL REFERENCES shopping_carts (id) ON DELETE CASCADE,
    product_id UUID    NOT NULL,
    quantity   BIGINT NOT NULL,
    PRIMARY KEY (cart_id, product_id)
);

CREATE INDEX IF NOT EXISTS idx_cart_items_cart_id ON shopping_cart_items (cart_id);
CREATE INDEX IF NOT EXISTS idx_cart_items_product_id ON shopping_cart_items (product_id);