CREATE TABLE IF NOT EXISTS payments
(
    id            UUID PRIMARY KEY,
    order_id      UUID            NOT NULL UNIQUE,
    state         VARCHAR         NOT NULL,
    product_price  NUMERIC(100, 2) NOT NULL,
    total_payment NUMERIC(100, 2) NOT NULL,
    delivery_total NUMERIC(100, 2) NOT NULL,
    fee_total     NUMERIC(100, 2) NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payments (order_id);
