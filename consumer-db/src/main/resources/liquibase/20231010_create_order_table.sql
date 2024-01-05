--liquibase formatted sql
--changeset orders:create-tables

CREATE SCHEMA kafka_orders AUTHORIZATION postgres;

CREATE TYPE kafka_orders.status_new AS ENUM ('IN_PROGRESS','CANCELED', 'APPROVED');

CREATE CAST (varchar AS kafka_orders.status_new) WITH INOUT AS IMPLICIT;

CREATE TABLE kafka_orders.orders
(
    id BIGSERIAL,
    product_name VARCHAR NOT NULL,
    bar_code VARCHAR NOT NULL,
    quantity INTEGER NOT NULL,
    price NUMERIC(19, 2) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    order_date TIMESTAMP NOT NULL,
    status kafka_orders.status_new NOT NULL,
    PRIMARY KEY (id)
);