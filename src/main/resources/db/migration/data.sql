insert into categories (category_name) values
('ОЗУ'),
('ЦПУ'),
('Накопитель'),
('Блок питания');

insert into order_status (status_name) values
('Создан'),
('В работе'),
('Готов к выдаче'),
('Выдан');

insert into customer (first_name, last_name, phone, email) values
('Йо', 'Асакура', '+1234567890','shaman@king.org'),
('Амидамару', '', '+1234567890', ''),
('Финн', 'Парнишка', '+2345678901', 'finn.human@adventuretime.com'),
('Джейк', 'Пёс', '+3456789012', 'jake.dog@adventuretime.com'),
('Морти', 'Смит', '+4567890123', 'morty.smith@rickandmorty.com'),
('Рик', 'Санчез', '+00000000000', 'wubbalubbadubdub@rickandmorty.com'),
('Скуби', 'Ду', '+5678901234', 'scooby.doo@hb.com'),
('Питер', 'Паркер', '+6789012345', 'your_friendly_neighbor@marvel.com'),
('Саске', 'Учиха', '+7890123456', 'sasuke.uchiha@konoha.com'),
('Мег', 'Гриффин', '+8901234567', 'megatron@familyguy.com');

insert into product (product_name, price, quantity, category_id) values
('DDR4 16GB 3200MHz', 4500.00, 25, 1),
('DDR5 32GB 4800MHz', 8900.00, 15, 1),
('Intel Core i7-13700K', 32000.00, 8, 2),
('AMD Ryzen 7 7800X3D', 28500.00, 10, 2),
('SSD NVMe 1TB', 5500.00, 30, 3),
('SSD SATA 512GB', 3200.00, 40, 3),
('HDD 2TB 7200RPM', 4500.00, 20, 3),
('Be Quiet! 750W 80+ Gold', 8900.00, 12, 4),
('Seasonic 850W 80+ Platinum', 12500.00, 8, 4),
('DDR4 8GB 2666MHz', 2500.00, 35, 1);

