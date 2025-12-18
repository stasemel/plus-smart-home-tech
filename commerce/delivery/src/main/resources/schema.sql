CREATE TABLE IF NOT EXISTS delivery
(
    id             UUID PRIMARY KEY,
    order_id       UUID         NOT NULL,
    delivery_state VARCHAR      NOT NULL,

    from_country   VARCHAR(100) NOT NULL,
    from_city      VARCHAR(200) NOT NULL,
    from_street    VARCHAR(200) NOT NULL,
    from_house     VARCHAR(100) NOT NULL,
    from_flat      VARCHAR(100) NOT NULL,

    to_country     VARCHAR(100) NOT NULL,
    to_city        VARCHAR(200) NOT NULL,
    to_street      VARCHAR(200) NOT NULL,
    to_house       VARCHAR(100) NOT NULL,
    to_flat        VARCHAR(100) NOT NULL,

    delivery_weight  NUMERIC(100, 2) NOT NULL,
    delivery_volume  NUMERIC(100, 2) NOT NULL,
    fragile          BOOLEAN         NOT NULL,
    delivery_price   NUMERIC(100, 2) NOT NULL

);