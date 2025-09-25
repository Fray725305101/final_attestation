--Создаём таблицу товаров
create table if not exists product (
    id serial primary key,
    product_name varchar(100) not null,
    price decimal(10,2) not null check (price >= 0),
    quantity integer not null check (quantity >= 0),
    category varchar(100) not null
);

--Создаём таблицу покупателей
create table if not exists customer (
    id serial primary key,
    customer_name varchar(1000) not null,
    phone varchar(20) not null,
    email varchar(300)
);

--Создаём таблицу статусов заказов
create table if not exists order_status (
    id serial primary key,
    order_name varchar(100) not null
);

--Создаём таблицу заголовков заказов
create table if not exists order_head (
    id serial primary key,
    customer_id integer not null,
    order_date timestamp not null default current_timestamp(),
    status_id integer not null,

    foreign key (customer_id) references customer(id),
    foreign key (status_id) references status(id)
);