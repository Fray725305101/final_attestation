--Создаём таблицу товаров
create table if not exists product (
    id serial primary key,
    product_name varchar(100) not null,
    price decimal(10,2) not null check (price >= 0),
    quantity integer not null check (quantity >= 0),
    category varchar(100) not null
);

--Создаём таблицу покупателей