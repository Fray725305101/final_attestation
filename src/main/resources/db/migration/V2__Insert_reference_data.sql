--Устанавливаем схему по умолчанию
SET search_path TO final_attestation_pakudin;

insert into categories (category_name) values
('RAM'),
('CPU'),
('Storage'),
('Power Supply');

insert into order_status (status_name) values
('Created'),
('In Progress'),
('Ready for Pickup'),
('Completed');