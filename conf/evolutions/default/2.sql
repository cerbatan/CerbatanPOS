# --- !Ups
CREATE TABLE orders (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  supplier TEXT NOT NULL,
  status NUMERIC(2) NOT NULL,
  invoice TEXT NOT NULL,
  received_timestamp TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_received_timestamp ON orders (received_timestamp);
CREATE INDEX idx_order_supplier ON orders (supplier);


CREATE TABLE order_items (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  order_id BIGINT NOT NULL,
  item BIGINT NOT NULL,
  cost NUMERIC(20,3) NOT NULL,
  price NUMERIC(20,3) NOT NULL,
  retail_price NUMERIC(20,3) NOT NULL,
  old_cost NUMERIC(20,3) NOT NULL,
  old_price NUMERIC(20,3) NOT NULL,
  old_retail_price NUMERIC(20,3) NOT NULL,

  FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE INDEX idx_order_item ON order_items(item);

CREATE TABLE order_fractions_updates (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  order_item BIGINT NOT NULL,
  fraction BIGINT NOT NULL,
  price NUMERIC(20,3) NOT NULL,
  old_price NUMERIC(20,3) NOT NULL,

  FOREIGN KEY (order_item) REFERENCES order_items(id)
);

CREATE INDEX idx_order_fraction_item ON order_fractions_updates(order_item);

# --- !Downs
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS order_fractions_updates;