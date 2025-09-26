--Устанавливаем схему по умолчанию
SET search_path TO final_attestation_pakudin;

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