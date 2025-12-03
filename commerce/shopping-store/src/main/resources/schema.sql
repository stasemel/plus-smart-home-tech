CREATE TABLE IF NOT EXISTS products
(
    product_id UUID,
    product_name VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    image_src VARCHAR NOT NULL,
    quantity_state VARCHAR NOT NULL,
    product_state VARCHAR NOT NULL,
    product_category VARCHAR NOT NULL,
    price NUMERIC(100,2) NOT NULL,
    CONSTRAINT pk_products PRIMARY KEY (product_id)
);
