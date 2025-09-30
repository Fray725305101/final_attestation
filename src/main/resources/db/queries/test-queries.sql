select p.product_name, sum(ob.quantity) as sum from final_attestation_pakudin.product p
join final_attestation_pakudin.order_body ob on p.id = ob.product_id
group by p.product_name
order by sum desc
limit 3;

select * from final_attestation_pakudin.product
where product_name like 'DDR%'
and price >= 3000;

select p.product_name, c.category_name from final_attestation_pakudin.product p
join final_attestation_pakudin.categories c on p.category_id = c.id;

select c.first_name,
       c.last_name,
       oh.order_date,
       os.status_name,
       c.phone
from final_attestation_pakudin.order_head oh
join final_attestation_pakudin.order_body ob on ob.head_id = oh.id
join final_attestation_pakudin.customer c on c.id = oh.customer_id
join final_attestation_pakudin.order_status os on os.id = oh.status_id
order by order_date desc;

select os.status_name, count(*) from final_attestation_pakudin.order_status os
join final_attestation_pakudin.order_head oh on os.id = oh.status_id
group by os.id
having count(*) > 2
order by os.id asc;

update final_attestation_pakudin.product
set price = price * 1.10
where category_id = 1;

update final_attestation_pakudin.order_head
set status_id = 4
where customer_id in (
    select id from final_attestation_pakudin.customer
    where last_name like 'Grif%'
    );

update final_attestation_pakudin.product
set quantity = quantity - ordered.ordered_quantity
from (
    select product_id, quantity as ordered_quantity
    from final_attestation_pakudin.order_body
    where head_id = 1
) as ordered
where final_attestation_pakudin.product.id = ordered.product_id
and final_attestation_pakudin.product.quantity >= ordered.ordered_quantity;