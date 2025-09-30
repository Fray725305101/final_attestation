select first_name, last_name from final_attestation_pakudin.customer;

select * from final_attestation_pakudin.product
where product_name like '%DDR%'
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