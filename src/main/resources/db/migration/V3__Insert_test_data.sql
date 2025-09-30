--Устанавливаем схему по умолчанию
SET search_path TO final_attestation_pakudin;

insert into customer (first_name, last_name, phone, email) values
('Yo', 'Asakura', '+1234567890','shaman@king.org'),
('Amidamaru', '', '+1234567890', ''),
('Finn', 'Human', '+2345678901', 'finn.human@adventuretime.com'),
('Jake', 'Dog', '+3456789012', 'jake.dog@adventuretime.com'),
('Morty', 'Smith', '+4567890123', 'morty.smith@rickandmorty.com'),
('Rick', 'Sanchez', '+0000000000', 'wubbalubbadubdub@rickandmorty.com'),
('Scooby', 'Doo', '+5678901234', 'scooby.doo@hb.com'),
('Peter', 'Parker', '+6789012345', 'your_friendly_neighbor@marvel.com'),
('Sasuke', 'Uchiha', '+7890123456', 'sasuke.uchiha@konoha.com'),
('Meg', 'Griffin', '+8901234567', 'megatron@familyguy.com');

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

insert into order_head (customer_id, order_date, status_id) values
(1, '2024-01-15 10:30:00', 4),
(2, '2024-07-16 14:20:00', 3),
(3, '2025-03-15 09:15:00', 2),
(4, '2025-07-18 16:45:00', 1),
(5, '2025-08-10 11:00:00', 4),
(6, '2025-08-20 13:30:00', 3),
(7, '2025-09-19 15:20:00', 2),
(8, '2025-09-22 10:00:00', 1),
(9, '2025-09-30 12:45:00', 4),
(10, '2025-09-30 17:30:00', 3);

insert into order_body (head_id, product_id, quantity, price) values
(1, 1, 2, 4500.00),
(1, 3, 1, 32000.00),
(2, 2, 1, 8900.00),
(3, 5, 1, 5500.00),
(3, 6, 2, 3200.00),
(4, 4, 1, 28500.00),
(5, 7, 1, 4500.00),
(5, 8, 1, 8900.00),
(6, 9, 1, 12500.00),
(7, 10, 4, 2500.00),
(8, 1, 1, 4500.00),
(8, 2, 1, 8900.00),
(9, 3, 1, 32000.00),
(10, 5, 2, 5500.00);