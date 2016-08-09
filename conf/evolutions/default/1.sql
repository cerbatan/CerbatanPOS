# --- !Ups
create table system_users (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  role TEXT NOT NULL,
  email TEXT NOT NULL,
  password TEXT NOT NULL,
  full_name TEXT
);

CREATE UNIQUE INDEX idx_userEmailId ON system_users (email);


CREATE TABLE brands (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  name TEXT NOT NULL
);

CREATE UNIQUE INDEX idx_brand_name ON brands (name);


CREATE TABLE items (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  sku TEXT NOT NULL,
  name TEXT NOT NULL,
  brand BIGINT NULL
);

CREATE UNIQUE INDEX idx_item_sku ON items (sku);
CREATE INDEX idx_item_name ON items (name);


CREATE TABLE tags (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  name TEXT NOT NULL
);
CREATE UNIQUE INDEX idx_tag_name ON tags (name);

CREATE TABLE tags_for_items (
  tag BIGINT NOT NULL,
  item BIGINT NOT NULL,
  PRIMARY KEY (tag, item),
  FOREIGN KEY (tag) REFERENCES tags(id),
  FOREIGN KEY (item) REFERENCES items(id)
);

CREATE TABLE taxes (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  name VARCHAR NOT NULL,
  percentage NUMERIC(5,2) NOT NULL
);

CREATE INDEX idx_tax_name ON taxes (name);

CREATE TABLE fractions (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  name TEXT NOT NULL,
  item BIGINT NOT NULL,
  price NUMERIC(20,3) NOT NULL,
  qty_in_pack INTEGER NOT NULL,
  FOREIGN KEY (item) REFERENCES items(id)
);

CREATE INDEX idx_fraction_item_id ON fractions(item);

CREATE TABLE items_stock (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  item BIGINT NOT NULL,
  cost NUMERIC(20,3) NOT NULL,
  price NUMERIC(20,3) NOT NULL,
  tax BIGINT NULL,
  retail_price NUMERIC(20,3) NOT NULL,
  track_stock BOOLEAN NOT NULL,
  stock_count NUMERIC(10,2) NOT NULL,
  alert_stock BOOLEAN NOT NULL,
  alert_level NUMERIC(10,2) NOT NULL,
  FOREIGN KEY (item) REFERENCES items(id)
);

CREATE SEQUENCE invoice_number_seq INCREMENT 1 START 1;

CREATE TABLE sales (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  datetime TIMESTAMP WITH TIME ZONE NOT NULL,
  invoice_number BIGINT NOT NULL DEFAULT nextval('invoice_number_seq')
);

CREATE INDEX idx_sale_datetime ON sales (datetime);
CREATE INDEX idx_sale_invoice_number ON sales (invoice_number);


CREATE TABLE sold_items (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  sale BIGINT NOT NULL,
  item_id BIGINT NOT NULL,
  fraction_id BIGINT NULL,
  count INTEGER NOT NULL,
  sold_price NUMERIC(20,3) NOT NULL,
  taxed NUMERIC(20,3) NOT NULL,
  cost NUMERIC(20,3) NOT NULL,
  FOREIGN KEY (sale) REFERENCES sales(id)
);

CREATE INDEX idx_sold_item_id ON sold_items(item_id);

# --- !Downs
DROP TABLE IF EXISTS system_users;

DROP TABLE IF EXISTS brands;

DROP TABLE IF EXISTS tags_for_items;

DROP TABLE IF EXISTS tags;

DROP TABLE IF EXISTS taxes;

DROP TABLE IF EXISTS fractions;

DROP TABLE IF EXISTS items_stock;

DROP TABLE IF EXISTS items;

DROP TABLE IF EXISTS sold_items;


DROP TABLE IF EXISTS  sales;


DROP SEQUENCE IF EXISTS invoice_number_seq;

