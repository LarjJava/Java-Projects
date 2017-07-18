select * from all_transactions

select * from text_files where account_number like '______-________'
select * from TEXT_FILES where acount_name
select count(*) from ALL_TRANSACTIONS
select count(*) from TEXT_FILES_VARCHAR
select count(*) from TEXT_FILES_TYPE

CREATE TABLE all_transactions_temp (file_id INTEGER NOT NULL,line_id INTEGER NOT NULL,date DATE,type VARCHAR(3),description VARCHAR(150),value DECIMAL(10,2),balance DECIMAL(10,2),account_name VARCHAR(30),account_number VARCHAR(20))

insert into ALL_TRANSACTIONS_TEMP select * from ALL_TRANSACTIONS

select * from categories
select * from ALL_TRANSACTIONS t, CATEGORIES c where t.description like c.pattern_match
select count(*) from ALL_TRANSACTIONS_categorised
select * from ALL_TRANSACTIONS_categorised

create view all_transactions_with_category
	as select t.date, t.type, t.description, t.value, t.balance, t.account_name, t.account_number, c.category_name, c.sub_category_name
	from ALL_TRANSACTIONS t, CATEGORIES c 
	where t.description like c.pattern_match

create view all_transactions_categorised
	as 
	select t.date, year(t.date) "TRANS_YEAR", month(t.date) "TRANS_MONTH", t.type, t.description, t.value, t.balance, t.account_name, t.account_number, c.category_name "CATEGORY_NAME", c.sub_category_name "SUB_CATEGORY_NAME"
	from ALL_TRANSACTIONS t left outer join CATEGORIES c 
	on t.description like c.pattern_match
	where t.type <> 'C/L'
	union
	select t.date, year(t.date) "TRANS_YEAR", month(t.date) "TRANS_MONTH", t.type, t.description, t.value, t.balance, t.account_name, t.account_number, 'Discretionary' "CATEGORY_NAME", 'Cash' "SUB_CATEGORY_NAME"
	from ALL_TRANSACTIONS t
	where type = 'C/L'
	
select trans_year, count(*) from ALL_TRANSACTIONS_CATEGORISED group by trans_year
select year from ALL_TRANSACTIONS_CATEGORISED
select count(*) from ALL_TRANSACTIONS group by file_id having file_id > 3 

select date from ALL_TRANSACTIONS_VIEW
	
select * from dual
select * from all_transactions order by date, file_id, line_id
select * from ALL_TRANSACTIONS order by description
select description, count(*) from ALL_TRANSACTIONS group by description order by count(*) desc
select description, count(*) from ALL_TRANSACTIONS group by description order by description
select * from ALL_TRANSACTIONS where description like 'MOBILE PHONE%'

select * from TEXT_FILES_TYPE order by date, file_id, line_id

select * from ALL_TRANSACTIONS at1, ALL_TRANSACTIONS at2 where at1.balance = at2.balance and at1.value = at2.value and at1.description = at2.description and at1.file_id <> at2.file_id order by at1.balance

insert into ALL_TRANSACTIONS (file_id, line_id, date, type, description, value, balance, account_name, account_number) select * from TEXT_FILES

delete from text_files where type = '' or type = ' Type'

select * from text_files where type = ' Type'


insert into ALL_TRANSACTIONS (file_id, line_id, date, type, description, value, balance, account_name, account_number) select
cast(file_id as integer), 
cast(line_id as integer), 
cast(substr(date,7,4) || '-' || substr(date,4,2) || '-' || substr(date,1,2) as date),
type,
description,
cast(value as decimal(10,2)),
cast(balance as decimal(10,2)),
account_name,
account_number
from TEXT_FILES where account_number like '______-________'

-- select substr(date,7,4) || '-' || substr(date,4,2) || '-' || substr(date,1,2) from TEXT_FILES

select * from ALL_TRANSACTIONS order by date, file_id, line_id
delete from ALL_TRANSACTIONS
delete from TEXT_FILES

select * from ALL_TRANSACTIONS_CATEGORISED
where trans_year = 2015 and trans_month = 10
order by sub_category_name
and category_name is null

select count(*) from ALL_TRANSACTIONS_CATEGORISED
select count(*) from ALL_TRANSACTIONS
select * from CATEGORIES
select * from ALL_TRANSACTIONS_CATEGORISED
select * from ALL_TRANSACTIONS

update all_transactions t
	set category_name = (select category_name from categories c where t.description like c.pattern_match)
	  , sub_category_name = (select sub_category_name from categories c where t.description like c.pattern_match)
  
select balance, description, count(description) from ALL_TRANSACTIONS_CATEGORISED
group by balance, description
having count(description) > 1
	  
select description, balance, count(*)
from all_transactions, categories
where description like pattern_match
group by description, balance
having count(*) > 1

select * from all_transactions
where category_id is null

where category_name = ''
select * from all_transactions
where category_name is null
order by description
select * from all_transactions
where description like '%MORRISONS%'
select * from all_transactions
where value = 89.39 or value = -89.39
select * from all_transactions
where description like '%PETROL%'
and category_name is null
select * from all_transactions
where type = 'BAC'
and category_name is null

select * from all_transactions
where category_name is not null
and date >= '2015-10-01' and date <= '2015-10-31'
order by value

select sub_category_name, sum(value)
from all_transactions
where category_name is not null
and date >= '2015-10-01' and date <= '2015-10-31'
group by sub_category_name
order by sum(value)

select * from all_transactions
where description like 'M COCKBURN%'

select * from categories

select * from all_transactions 
WHERE type = 'BAC'
AND description NOT LIKE 'PE153158C DWP DLA%'
AND description NOT LIKE '%-CHB%'
order by date

select * from all_transactions 
WHERE description LIKE 'PE153158C DWP DLA%'
OR description LIKE '%-CHB%'
order by description

select * from all_transactions
where (description like '9598%'
or description like '8808%'
or description like '5668%'
or description like '5646%'
or description like '3187%'
or description like '2867%'
or description like '2463%'
or description like '2416%'
or description like '2370%'
or description like '1669%'
or description like '1586%')
and category_name is null

CREATE TABLE categories_ooo (category_id INTEGER NOT NULL PRIMARY KEY
							GENERATED ALWAYS AS IDENTITY
						  	(START WITH 1, INCREMENT BY 1),
						 category_name VARCHAR(30),
						 sub_category_name VARCHAR(30));

select * from CATEGORIES

MERGE INTO categories_ooo co
USING categories c
ON c.category_name = co.category_name
AND c.sub_category_name = co.sub_category_name
WHEN NOT MATCHED THEN INSERT (category_name, sub_category_name) VALUES ( c.category_name );

insert into categories (
	select 1515,'paul_cat' as category_name,'paul_sub_cat' as sub_category_name,'paul_pattern'
	from categories
	where category_name = 'paul_cat' and sub_category_name = 'paul_sub_cat'
	having count(*) = 0);

insert into categories (
	select 1530,'paul_cat15','paul_sub_cat','paul_pattern'
	from categories
	where category_name = 'paul_cat15' and sub_category_name = 'paul_sub_cat'
	having count(*) = 0);	

insert into categories (category_name, sub_category_name) (
	select 'paul_cat','paul_sub_cat'
	from categories
	where category_name = 'paul_cat' and sub_category_name = 'paul_sub_cat'
	having count(*) = 0);	

select * from categories

select * from category_map

INSERT INTO category_map
(category_id, pattern_match)
values
((select category_id from categories where category_name = 'Income' and sub_category_name = 'Salary'),'SALARY%')

select 'paul_cat' as 
	
select * from text_files_varchar

create view category_map_view
as select c.category_id, c.category_name, c.sub_category_name, cm.pattern_match
from categories c, category_map cm
where cm.category_id = c.category_id

select * from category_map_view
WHERE category_name = 'Discretionary'
AND sub_category_name = 'Cashline'

WHERE type = 'C/L'

create view all_transactions_view
as 
select atr.*, c.category_name, c.sub_category_name
from all_transactions atr left outer join categories c
on c.category_id = atr.category_id

select * from all_transactions_view
where category_id is null

select distinct account_number from ALL_TRANSACTIONS_VIEW

select * from all_transactions_view where description like '%PE1%'

select * from IGNORE_TRANSACTIONS

SELECT description, balance, count(*)
						  FROM all_transactions t, categories c
						  WHERE t.description like c.pattern_match
						  GROUP BY description, balance
						  HAVING count(*) > 1;

select * from categories

select * from category_map_view

select * from all_transactions where type not in ('BAC','C/L')

select * from category_map where trans_type = ''

select * from all_transactions_view where value > -530 and value < -520

select date, YEAR({fn timestampadd(SQL_TSI_DAY,-15,date)}) RBS_YEAR, MONTH({fn timestampadd(SQL_TSI_DAY,-15,date)}) RBS_MONTH from ALL_TRANSACTIONS_VIEW

select cal_year, cal_month, sub_category_name, sum(value) from ALL_TRANSACTIONS_VIEW
group by cal_year, cal_month, sub_category_name
order by cal_year, cal_month, sum(value)

select cal_year, sub_category_name, sum(value)/12
from all_transactions_view
where sub_category_name like 'Roa%'
group by cal_year, sub_category_name

select * from all_transactions_view where sub_category_name = 'Road Tax'
order by cal_year

select * from uncategorised_view

select * from ALL_TRANSACTIONS_VIEW where value = 50.01

select sum(value) from ALL_TRANSACTIONS_VIEW
where rbs_year = 2015
and rbs_month = 9
and value > 0
and category_id is not null
union
select sum(value) from ALL_TRANSACTIONS_VIEW
where rbs_year = 2015
and rbs_month = 9
and value <= 0
and category_id is not null
union
select sum(value) from ALL_TRANSACTIONS_VIEW
where rbs_year = 2015
and rbs_month = 9
and category_id is not null

order by date desc, line_id desc

select distinct account_name from all_transactions

select distinct account_name from TEXT_FILES_VARCHAR

select * from TEXT_FILES_VARCHAR where account_name like '__-__-__'

select * from TEXT_FILES_VARCHAR
order by account_name

where account_name like '__-__-__'

select * from all_transactions where account_name like '__-__-__'

select * from TEXT_FILES_TYPE
where account_name like '__-__-__'

SELECT * FROM text_files_type ORDER BY account_name, date, line_id, file_id

select * from ALL_TRANSACTIONS where account_name like '__-__-__'

select * from all_transactions where description like '%DUDDING%'

select * from all_transactions where value = -34.68 or value = 34.68

select * from all_transactions where bank = 'BOS' order by line_id

SELECT * FROM text_files_type 
WHERE account_number LIKE '______-________'
ORDER BY date, line_id, file_id

SELECT * FROM text_files_type 
WHERE account_name LIKE '__-__-__'
ORDER BY date, line_id desc, file_id

select * from all_transactions where bank = 'BOS'
and type = 'BGC'
order by date

select distinct type from all_transactions where bank = 'BOS'

select count(*) from ALL_TRANSACTIONS

describe text_files_type

select * from all_transactions

select * from all_transactions
order by date desc


select * from all_transactions_view
order by date desc

select sum(value) from all_transactions_view where category_name = 'Income'

select count(*) from UNCATEGORISED_VIEW

CREATE VIEW category_spend_by_month_view (cal_year, cal_month, category_name, sub_category_name, sum_value) AS
SELECT cal_year, cal_month, category_name, sub_category_name, sum(value)
FROM all_transactions_view
GROUP BY cal_year, cal_month, category_name, sub_category_name

ORDER BY cal_year, cal_month, SUM(VALUE)

select * from category_spend_by_month_view

select * from category_spend_by_month_view

SELECT cal_year, category_name, sub_category_name, sum(value)/12
FROM all_transactions_view
GROUP BY cal_year, category_name, sub_category_name

select * from all_transactions_view
where bank = 'BOS' 
and balance = 3304.32

SELECT * FROM text_files_type
WHERE account_name LIKE '__-__-__'
ORDER BY date, line_id DESC, file_id

select *
from all_transactions_view
where bank = 'BOS'
order by date desc, line_id

select count(*) from all_transactions

select * from all_transactions where bank = 'BOS_CC'
and date > '01/10/2015'

SELECT * FROM text_files_type where balance is null

SELECT * FROM text_files_type
where type = 'MCC'

where bank = 'BOS_CC'

SELECT distinct bank FROM text_files_varchar

SELECT * FROM text_files_varchar
where date like '%Date%' or date = ''

select * from CATEGORIES

select date from all_transactions

select * from CATEGORY_MAP_VIEW
where pattern_match like '%MORRISONS%'

select * from all_transactions where type = 'MCC'

select 	atv.sub_category_name
from	(select distinct sub_category_name from all_transactions_view) atv

select distinct category from all_transactions_view

select * from
(
select 		atv.sub_category_name, 
			(select sum(atv1.value) value
				from all_transactions_view atv1
				where atv1.sub_category_name = atv.sub_category_name
				and atv1.rbs_year = 2016
				and atv1.rbs_month = 1) this_month,
			(select sum(atv1.value) value
				from all_transactions_view atv1
				where atv1.sub_category_name = atv.sub_category_name
				and atv1.rbs_year = 2015
				and atv1.rbs_month = 12) last_month
from		(select distinct sub_category_name 
				from all_transactions_view
				where category_name IN ('Bills','Discretionary')) atv
) grid
where		this_month is not null or last_month is not null 
order by	this_month

select * from all_transactions_view where value = -6

select * from all_transactions_view where description like '%BANK OF SCOTLAND M%'
select * from all_transactions_view where description like '%CREDIT CARD%'
select * from all_transactions_view where description like '%DIRECT LINE%'

select * from all_transactions_view where type in ('CHG','INT') and bank = 'RBS'
select * from all_transactions_view where type = 'FPO'

select * from all_transactions_view where bank = 'BOS' and rbs_year <= 2007
select rbs_month, sum(value) from all_transactions_view where sub_category_name = 'Miscellaneous' and rbs_year = 2015
group by rbs_month

select * from all_transactions_view where sub_category_name = 'Miscellaneous' and rbs_year = 2015 and rbs_month = 3

select * from all_transactions_view where rbs_months_ago = 1

select *
from all_transactions_view
where category_name is not null
and category_name <> 'Credit Card'

select MONTH({fn TIMESTAMPADD(SQL_TSI_DAY,-15,current_timestamp)}) - rbs_month + (YEAR({fn TIMESTAMPADD(SQL_TSI_DAY,-15,current_timestamp)}) - rbs_year) * 12
from all_transactions_view atv

select MONTH({fn TIMESTAMPADD(SQL_TSI_DAY,-15,CURRENT_TIMESTAMP)}) - MONTH({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)}) + (YEAR({fn TIMESTAMPADD(SQL_TSI_DAY,-15,CURRENT_TIMESTAMP)}) - YEAR({fn TIMESTAMPADD(SQL_TSI_DAY,-15,date)})) * 12, atv.*
from all_transactions_view atv

select * from all_transactions_view where sub_category_name like 'TV Su%' order by date desc



where rbs_year = year({fn TIMESTAMPADD(SQL_TSI_DAY,-15,current_timestamp)})
and rbs_month = month({fn TIMESTAMPADD(SQL_TSI_DAY,-15,current_timestamp)})
and category_name <> 'Credit Card'

select 1-2 from categories

SELECT *
FROM all_transactions_view
WHERE category_name IS NOT NULL
AND category_name <> 'Credit Card'
AND rbs_months_ago IN (0,1,12) 

SELECT 'Income', 'Child Benefit'
FROM categories
WHERE category_name = 'Income' AND sub_category_name = 'Child Benefit'
HAVING COUNT(*) = 1

select * from categories

select {fn timestampadd(SQL_TSI_DAY,-15,date)} from ALL_TRANSACTIONS_VIEW

select {fn monthname(current_timestamp)} from CATEGORIES

select 	   case 
		   when rbs_month = 1 then 'Jan'
		   when rbs_month = 2 then 'Feb'
		   when rbs_month = 3 then 'Mar'
		   when rbs_month = 4 then 'Apr'
		   when rbs_month = 5 then 'May'
		   when rbs_month = 6 then 'Jun'
		   when rbs_month = 7 then 'Jul'
		   when rbs_month = 8 then 'Aug'
		   when rbs_month = 9 then 'Sep'
		   when rbs_month = 10 then 'Oct'
		   when rbs_month = 11 then 'Nov'
		   when rbs_month = 12 then 'Dec'
		   end rbs_month_name
	   from all_transactions_view

select * from ALL_TRANSACTIONS_VIEW

select cmv.* from CATEGORY_MAP_VIEW cmv, ALL_TRANSACTIONS atr
where cmv.category_id = (
SELECT cmv2.category_id FROM category_map_view cmv2 
WHERE atr.type = cmv2.trans_type
AND atr.description LIKE cmv2.pattern_match) 
AND atr.type = 'BAC'

			String psString2 = "UPDATE all_transactions t "
					 		 + "SET category_id = ("
					 		 + "SELECT category_id FROM category_map_view "
					 		 + "WHERE t.type = trans_type "
			 				 + "AND t.type IN ('CHG','INT','C/L','CHQ','PAY','DEB','CPT','MCC')) "
			 				 + "WHERE type IN ('CHG','INT','C/L','CHQ','PAY','DEB','CPT','MCC')";


select * from CATEGORY_MAP_VIEW
where trans_type = 'BAC'

select * from all_transactions t
where type = 'BAC'

select * from all_transactions_view where description like '%SLBS%'

select * from ALL_TRANSACTIONS_VIEW where bank = 'OTHER'

SELECT atr.balance, 'Joint Account' Account_Name
FROM all_transactions atr
WHERE bank = 'RBS'
AND date = (SELECT MAX(date) FROM all_transactions WHERE bank = 'RBS')
AND line_id = (SELECT MAX(line_id) FROM all_transactions 
			   WHERE bank = 'RBS'
			   AND date = (SELECT MAX(date) FROM all_transactions WHERE bank = 'RBS'))
UNION
SELECT atr.balance, 'Maureens Account' Account_Name
FROM all_transactions atr
WHERE bank = 'BOS'
AND date = (SELECT MAX(date) FROM all_transactions WHERE bank = 'BOS')
AND line_id = (SELECT MIN(line_id) FROM all_transactions 
			   WHERE bank = 'BOS' 
			   AND date = (SELECT MAX(date) FROM all_transactions WHERE bank = 'BOS'))
UNION
SELECT atr.balance, 'Credit Card' Account_Name
FROM all_transactions atr
WHERE bank = 'BOS_CC'
AND date = (SELECT MAX(date) FROM all_transactions WHERE bank = 'BOS_CC')
AND line_id = (SELECT MIN(line_id) FROM all_transactions 
			   WHERE bank = 'BOS_CC' 
			   AND date = (SELECT MAX(date) FROM all_transactions WHERE bank = 'BOS_CC'))
UNION
SELECT atr.balance, 'Mortgage' Account_Name
FROM all_transactions atr
WHERE bank = 'OTHER'
AND DESCRIPTION = 'Mortgage Balance'

select * from all_transactions where bank = 'BOS_CC'

SELECT atr.balance, 'Joint Account' Account_Name
FROM all_transactions atr
WHERE bank = 'RBS'
AND transaction_id = (SELECT MAX(transaction_id) FROM all_transactions WHERE bank = 'RBS')
UNION
SELECT atr.balance, 'Maureens Account' Account_Name
FROM all_transactions atr
WHERE bank = 'BOS'
AND transaction_id = (SELECT MAX(transaction_id) FROM all_transactions WHERE bank = 'BOS')
UNION
SELECT atr.balance, 'Credit Card' Account_Name
FROM all_transactions atr
WHERE bank = 'BOS_CC'
AND transaction_id = (SELECT MAX(transaction_id) FROM all_transactions WHERE bank = 'BOS_CC')
UNION
SELECT atr.balance, 'Mortgage' Account_Name
FROM all_transactions atr
WHERE bank = 'OTHER'
AND DESCRIPTION = 'Mortgage Balance'

select * from accounts

select * from all_transactions where value > 1000
and description not like '%SALA%' 
and description not like '%MAU%'  
and description not like '%COCK%'
and description not like '%CITY%'
order by date

select sum(value) from all_transactions_view where rbs_year = 2015 and rbs_month = 12 and sub_category_name like 'Miscellaneous%' and bank IN ('BOS','RBS')

select * from all_transactions_view where value = -50.64

select
case when type = 'AMX' then 'paul' else 'cockburn' end
-- decode(type,'AMX',substr(date,7,4) || '-' || substr(date,4,2) || '-' || substr(date,1,2))
select * from TEXT_FILES_VARCHAR

select * from all_transactions_view

select atv1.bank, atv1.rbs_year, atv1.rbs_month, atv1.balance 
from ALL_TRANSACTIONS_VIEW atv1,
	(select bank, rbs_year, rbs_month, max(transaction_id) transaction_id
	 from ALL_TRANSACTIONS_VIEW
	 group by bank, rbs_year, rbs_month) atv2
where atv1.transaction_id = atv2.transaction_id

and atv1.bank = 'AMEX'

union
select bank, balance from ALL_TRANSACTIONS where transaction_id = (select max(transaction_id) from ALL_TRANSACTIONS where bank = 'BOS')

CREATE TABLE monthly_balance_transactions
AS SELECT bank, rbs_year, rbs_month, transaction_id
FROM ALL_TRANSACTIONS_VIEW
WITH NO DATA

INSERT INTO monthly_balance_transactions
SELECT bank, rbs_year, rbs_month, MAX(transaction_id) transaction_id
FROM all_transactions_view
GROUP BY bank, rbs_year, rbs_month

CREATE VIEW monthly_balances_view
AS SELECT atv.bank, atv.rbs_year, atv.rbs_month, atv.rbs_months_ago, atv.rbs_month_name, atv.balance 
FROM ALL_TRANSACTIONS_VIEW atv, MONTHLY_BALANCE_TRANSACTIONS mbt
WHERE atv.transaction_id = mbt.transaction_id
	 
drop table MONTHLY_BALANCE_TRANSACTIONS
drop view MONTHLY_BALANCES_VIEW

select * from MONTHLY_BALANCES_VIEW

select rbs_month_name || ' ' || CAST(rbs_year as CHAR(4)) FROM all_transactions_view

select * from ALL_TRANSACTIONS_VIEW
where type = 'MCC'

select * from MONTHLY_BALANCE_TRANSACTIONS

select * from TEXT_FILES_VARCHAR
select * from TEXT_FILES_TYPE

commit;

select distinct account_id from all_transactions
select distinct account_id from text_files_varchar
select distinct account_id from text_files_type

select * from categories

select * from all_transactions_view where description like '%MACKINNON MOTOR%' or description like '%MOTORS%'

SELECT category_id FROM category_map_view 
WHERE 'PAYPAL *ME 08453642564' LIKE pattern_match
AND trans_type NOT IN ('BAC','CHG','INT','C/L','CHQ','PAY','CPT','MCC')

select * from all_transactions where type = 'ADJ'
select * from all_transactions where value = -73.99 or value = 73.99

select * from all_transactions where type = 'ADJ'
select * from all_transactions where value = -73.99 or value = 73.99

select cast(SUBSTR(date,7,4) || '-' || SUBSTR(date,4,2) || '-' || SUBSTR(date,1,2) as date)

select * from text_files_varchar
where account_id = '2'

select * from text_files_type
where account_id = 2
ORDER BY date, line_id DESC, file_id

select * from all_transactions
where account_id = 2

select distinct(rbs_months_ago) from all_transactions_view

where substr(date,4,1) not in ('0','1','2','3','4','5','6','7','8','9')

cast(SUBSTR(date,7,4) || '-' || SUBSTR(date,4,2) || '-' || SUBSTR(date,1,2) as date)

select * from monthly_balance_transactions
select * from monthly_balances_view where account_id = 4

SELECT account_id, rbs_year, rbs_month, MAX(transaction_id) transaction_id
FROM all_transactions_view
GROUP BY account_id, rbs_year, rbs_month

select * from all_transactions_view where transaction_id = 198

select * from TEXT_FILES_VARCHAR where account_id = '2' and date like '%2016'

SELECT * FROM  all_transactions t
WHERE t.description LIKE '%PAYPAL%NGOTT%35314369001%'

select * from ACCOUNTS

select * from all_transactions_view where account_id = 10

select * from all_transactions
where description like '%DEER%'

select * from CATEGORY_MAP_VIEW

select * from ALL_TRANSACTIONS
where account_id = 2

select * from text_files_varchar
where account_id = '2'
and date like '%05%2017'

select * from text_files_type
where account_id = 2
and date > '05/01/2017'
ORDER BY date, line_id DESC, file_id

SELECT * FROM text_files_type WHERE account_id = 2 ORDER BY date, line_id DESC, file_id





























