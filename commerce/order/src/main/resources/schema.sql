CREATE TABLE IF NOT EXISTS orders
(
    id               UUID PRIMARY KEY,
    user_name        VARCHAR         NOT NULL,
    shopping_cart_id UUID            NOT NULL UNIQUE,
    payment_id       UUID            NOT NULL,
    delivery_id      UUID            NOT NULL,
    state            VARCHAR         NOT NULL,
    delivery_weight  NUMERIC(100, 2) NOT NULL,
    delivery_volume  NUMERIC(100, 2) NOT NULL,
    fragile          BOOLEAN         NOT NULL,
    total_price      NUMERIC(100, 2) NOT NULL,
    delivery_price   NUMERIC(100, 2) NOT NULL,
    product_price    NUMERIC(100, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS orders_products
(
    order_id   UUID   NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    product_id UUID   NOT NULL,
    quantity   BIGINT NOT NULL,
    PRIMARY KEY (order_id, product_id)
);

CREATE INDEX IF NOT EXISTS idx_orders_username ON orders (user_name);
CREATE INDEX IF NOT EXISTS idx_orders_shopping_cart_id ON orders (shopping_cart_id);
CREATE INDEX IF NOT EXISTS idx_orders_products_product_id ON orders_products (product_id);