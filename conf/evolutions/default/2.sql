# --- !Ups
CREATE TABLE orders (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  supplier TEXT NOT NULL
  status NUMERIC(2) NOT NULL,
  invoice TEXT NOT NULL
  received_timestamp TIMESTAMP WITH TIME ZONE,
);

CREATE INDEX idx_received_timestamp ON orders (received_timestamp);
CREATE INDEX idx_order_supplier ON orders (supplier);


CREATE TABLE order_items (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  order BIGINT NOT NULL,
  item BIGINT NOT NULL,
  cost NUMERIC(20,3) NOT NULL,
  price NUMERIC(20,3) NOT NULL,
  retail_price NUMERIC(20,3) NOT NULL,
  old_cost NUMERIC(20,3) NOT NULL,
  old_price NUMERIC(20,3) NOT NULL,
  old_retail_price NUMERIC(20,3) NOT NULL,

  FOREIGN KEY (order) REFERENCES order(id)
);

CREATE INDEX idx_order_item ON order_items(item);

# --- !Downs
DROP TABLE IF EXISTS  orders;
DROP TABLE IF EXISTS  order_items;