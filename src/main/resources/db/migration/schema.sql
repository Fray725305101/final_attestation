--Для создания БД
--CREATE DATABASE final_attestation_pakudin
--WITH ENCODING 'UTF8'
--LC_COLLATE 'Russian_Russia.1251'
--LC_CTYPE 'Russian_Russia.1251'
--TEMPLATE template0;

--Создаём схему, в которой будем работать
create schema if not exists final_attestation_pakudin authorization postgres;

--Создаём таблицу категорий
create table if not exists categories (
    id serial primary key,
    category_name varchar(100) not null
);

--Создаём таблицу товаров
create table if not exists product (
    id serial primary key,
    product_name varchar(1000) not null,
    price decimal(10,2) not null check (price > 0),
    quantity integer not null check (quantity >= 0),
    category_id integer not null,

    foreign key (category_id) references categories(id)
);

--Создаём таблицу покупателей
create table if not exists customer (
    id serial primary key,
    first_name varchar(100),
    last_name varchar(100),
    phone varchar(20) not null,
    email varchar(300),

    --Проверяем, что заполнены хотя бы имя или фамилия
    check (
        (first_name is not null and first_name != '') or
        (last_name is not null and last_name != '')
    )
);

--Создаём таблицу статусов заказов
create table if not exists order_status (
    id serial primary key,
    status_name varchar(100) not null
);

--Создаём таблицу заголовков заказов
create table if not exists order_head (
    id serial primary key,
    customer_id integer not null,
    order_date timestamp not null default current_timestamp,
    status_id integer not null,

    foreign key (customer_id) references customer(id),
    foreign key (status_id) references order_status(id)
);

--Создаём таблицу тела заказов
create table if not exists order_body (
    id serial primary key,
    head_id integer not null,
    product_id integer not null,
    quantity integer not null check (quantity > 0),
    price decimal(10,2) not null check (price > 0),

    foreign key (head_id) references order_head(id),
    foreign key (product_id) references product(id)
);

--Индексы:
create index if not exists idx_order_head_customer_id on order_head(customer_id);
create index if not exists idx_order_head_status_id on order_head(status_id);
create index if not exists idx_order_head_order_date on order_head(order_date);
create index if not exists idx_order_body_head_id on order_body(head_id);
create index if not exists idx_order_body_product_id on order_body(product_id);