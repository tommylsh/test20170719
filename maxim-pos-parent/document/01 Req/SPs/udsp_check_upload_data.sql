CREATE   PROCEDURE [dbo].[udsp_chk_upload_data]
@business_date	 char(8),
@branch_code	varchar(6),
--Carl
@bal_controller	varchar(3)
--Carl
AS

declare @supp_table		varchar(50)
declare @supp_value01	money
declare @supp_value02	money
declare @supp_value03	money
declare @comp_value01	money
declare @comp_value02	money
declare @comp_value03	money

----- Temporary for fix foreign currency
--SELECT cury_no, tender AS amt, pay_amt AS local_amt INTO #income FROM hist_orders_pay (NOLOCK) WHERE 1 = 2
--SELECT * INTO #orders_pay FROM hist_orders_pay (NOLOCK) WHERE branch_code = @branch_code AND business_date = @business_date AND cury_no <> '344' AND card_type <> 'SBUX'
 
--INSERT INTO #income SELECT cury_no, ISNULL(SUM(tender + tips), 0), ISNULL(SUM(pay_amt + change), 0) FROM #orders_pay WHERE recall = 0 AND refund = '0' AND void = '0' GROUP BY cury_no

--INSERT INTO #income SELECT cury_no, ISNULL(SUM(tender + tips), 0) * -1, ISNULL(SUM(pay_amt + change), 0) * -1 FROM #orders_pay WHERE recall = 0 AND refund = '1' AND void = '0' GROUP BY cury_no

--IF (SELECT SUM(local_amt) FROM #income) <> (SELECT SUM(case left(type, 1) when 'E' then local_amt * -1 else local_amt end) FROM hist_payfig (NOLOCK) WHERE branch_code = @branch_code AND business_date = @business_date AND cury_no <> '344')
--	return

--drop table #income
--drop table #orders_pay
----- Temporary for fix foreign currency

----- Temporary for fix Temporary Counter
declare @poll_method	char(2)

select @poll_method = poll_method from poll_branch_info (nolock) where bran_code = @branch_code
----- Temporary for fix Temporary Counter

if not exists (select top 1 * from hist_supp (nolock) where business_date = @business_date and branch_code = @branch_code and supp_type = 'SUM' and txt1 = 'hist_supp') return

--Carl
/*
select @supp_value01 = val1 from hist_supp (nolock) where business_date = @business_date and branch_code = @branch_code and supp_type = 'SUM' and txt1 = 'hist_supp'
select @comp_value01 = count(*) from hist_supp (nolock) where business_date = @business_date and branch_code = @branch_code
if @supp_value01 <> @comp_value01 return
*/


--CC
select @supp_value01 = count(*) from hist_supp (nolock) where business_date = @business_date and branch_code = @branch_code and supp_type = 'SMM'

if @bal_controller = 'YES'
	if exists (select * from branch_inventory_info (nolock) where business_date = @business_date and branch_code = @branch_code)
		if (@supp_value01 = 0) return

--select @supp_value01 = val1 from hist_supp (nolock) where business_date = @business_date and branch_code = @branch_code and supp_type = 'SUM' and txt1 = 'hist_supp'
select @supp_value01 = @supp_value01 + val1 from hist_supp (nolock) where business_date = @business_date and branch_code = @branch_code and supp_type = 'SUM' and txt1 = 'hist_supp'
--CC
--Carl

select @comp_value01 = count(*) from hist_supp (nolock) where business_date = @business_date and branch_code = @branch_code and supp_type <> 'CMM'
if @supp_value01 <> @comp_value01 return


declare chk_table_cursor CURSOR
	for
--Carl	 
--		select txt1, val1, val2, val3 from hist_supp (nolock) where business_date = @business_date and branch_code = @branch_code and supp_type = 'SUM'
--CC
		select txt1, val1, val2, val3 from hist_supp (nolock) where business_date = @business_date and branch_code = @branch_code and supp_type in ('SUM', 'SMM')
--CC
--Carl


open chk_table_cursor 

fetch chk_table_cursor into @supp_table, @supp_value01, @supp_value02, @supp_value03

while (@@FETCH_STATUS <> -1)
	begin
		if @supp_table = 'hist_check_logs'
			begin
				select @comp_value01 = count(*) from hist_check_logs (nolock) where business_date = @business_date and branch_code = @branch_code
				if @supp_value01 <> @comp_value01 goto chk_rec_count_error
			end
	
		if @supp_table = 'hist_coupon_sales'
			begin
				select @comp_value01 = count(*), @comp_value02 = sum(coupon_qty) from hist_coupon_sales (nolock) where business_date = @business_date and branch_code = @branch_code
				if @supp_value01 <> @comp_value01 or @supp_value02 <> @comp_value02 goto chk_rec_count_error
			end
	
		if @supp_table = 'hist_item'
			begin
				select @comp_value01 = count(*) from hist_item (nolock) where business_date = @business_date and branch_code = @branch_code
				
----- Temporary for fix Temporary Counter
				if @poll_method = '3'
					begin
						if @supp_value01 > @comp_value01 goto chk_rec_count_error
					end
				else
----- Temporary for fix Temporary Counter
					begin				
						if @supp_value01 <> @comp_value01 goto chk_rec_count_error
					end
			end

		if @supp_table = 'hist_itemstock'
			begin
				select @comp_value01 = count(*), @comp_value02 = sum(diff_amt) from hist_itemstock (nolock) where business_date = @business_date and branch_code = @branch_code

----- Temporary for fix Temporary Counter		
				if @poll_method = '3'
					begin
						if @supp_value01 > @comp_value01 or @supp_value02 <> @comp_value02 goto chk_rec_count_error
					end
				else
----- Temporary for fix Temporary Counter
					begin							
						if @supp_value01 <> @comp_value01 or @supp_value02 <> @comp_value02 goto chk_rec_count_error
					end
			end

		if @supp_table = 'hist_orders'
			begin
				select @comp_value01 = count(*), @comp_value02 = sum(grand_total) from hist_orders (nolock) where business_date = @business_date and branch_code = @branch_code
				if @supp_value01 <> @comp_value01 or @supp_value02 <> @comp_value02 goto chk_rec_count_error
			end

		if @supp_table = 'hist_orders_extra'
			begin
				select @comp_value01 = count(*), @comp_value02 = sum(deposit), @comp_value03 = sum(remain) from hist_orders_extra (nolock) where business_date = @business_date and branch_code = @branch_code
				if @supp_value01 <> @comp_value01 or @supp_value02 <> @comp_value02 or @supp_value03 <> @comp_value03 goto chk_rec_count_error
			end

		if @supp_table = 'hist_orders_pay'
			begin
				select @comp_value01 = count(*), @comp_value02 = sum(pay_amt), @comp_value03 = sum(tips) from hist_orders_pay (nolock) where business_date = @business_date and branch_code = @branch_code
				if @supp_value01 <> @comp_value01 or @supp_value02 <> @comp_value02 or @supp_value03 <> @comp_value03 goto chk_rec_count_error
			end

		if @supp_table = 'hist_orders_pay_prg'
			begin
				select @comp_value01 = count(*), @comp_value02 = sum(pay_amt), @comp_value03 = sum(tips) from hist_orders_pay_progress (nolock) where business_date = @business_date and branch_code = @branch_code
				if @supp_value01 <> @comp_value01 or @supp_value02 <> @comp_value02 or @supp_value03 <> @comp_value03 goto chk_rec_count_error
			end

		if @supp_table = 'hist_payfig'
			begin
				select @comp_value01 = count(*), @comp_value02 = sum(input_amt) from hist_payfig (nolock) where business_date = @business_date and branch_code = @branch_code
				if @supp_value01 <> @comp_value01 or @supp_value02 <> @comp_value02 goto chk_rec_count_error
			end

		if @supp_table = 'hist_paysum'
			begin
				select @comp_value01 = count(*), @comp_value02 = sum(total_amt) from hist_paysum (nolock) where business_date = @business_date and branch_code = @branch_code
				if @supp_value01 <> @comp_value01 or @supp_value02 <> @comp_value02 goto chk_rec_count_error
			end

		if @supp_table = 'hist_possystem'
			begin
				select @comp_value01 = count(*) from hist_possystem (nolock) where business_date = @business_date and branch_code = @branch_code
				if @supp_value01 <> @comp_value01 goto chk_rec_count_error
			end

		if @supp_table = 'hist_redeemed_coupon'
			begin
				select @comp_value01 = count(*) from hist_redeemed_coupon (nolock) where business_date = @business_date and branch_code = @branch_code
				if @supp_value01 <> @comp_value01 goto chk_rec_count_error
			end

		if @supp_table = 'hist_trans'
			begin
				select @comp_value01 = count(*), @comp_value02 = sum(sub_total) from hist_trans (nolock) where business_date = @business_date and branch_code = @branch_code
				if @supp_value01 <> @comp_value01 or @supp_value02 <> @comp_value02 goto chk_rec_count_error
			end

		if @supp_table = 'hist_trans_ecard'
			begin
				select @comp_value01 = count(*), @comp_value02 = sum(addvalue_amt) from hist_trans_ecard (nolock) where business_date = @business_date and branch_code = @branch_code
				if @supp_value01 <> @comp_value01 or @supp_value02 <> @comp_value02 goto chk_rec_count_error
			end

		if @supp_table = 'hist_trans_modifier'
			begin
				select @comp_value01 = count(*), @comp_value02 = sum(modif_total_amt) from hist_trans_modifier (nolock) where business_date = @business_date and branch_code = @branch_code
				if @supp_value01 <> @comp_value01 or @supp_value02 <> @comp_value02 goto chk_rec_count_error
			end

			fetch chk_table_cursor into @supp_table, @supp_value01, @supp_value02, @supp_value03	end

--Multiple Business Date Checking-----------------------------------
declare @cut_off_start_datetime	as datetime
declare @cut_off_end_datetime	as datetime

select @cut_off_start_datetime = dateadd(hh, 3, dateadd(dd, -1, @business_date))

select @cut_off_end_datetime = dateadd(hh, 3, @business_date)

--select @business_date, @cut_off_start_datetime, @cut_off_end_datetime

if exists (select top 1 * from hist_orders (nolock) where business_date = @business_date and branch_code = @branch_code and 
--	trans_datetime between @cut_off_start_datetime and @cut_off_end_datetime)
	trans_datetime < @cut_off_end_datetime)
begin
	if not exists (select * from md_dayend_control_table (nolock) where branch_code = @branch_code and final_business_date = '19000101' and status = 'C')
	begin
		if not exists (select * from md_dayend_control_table (nolock) where branch_code = @branch_code and final_business_date = @business_date and status = 'C')
		begin
			if not exists (select * from md_dayend_control_table (nolock) where branch_code = @branch_code and final_business_date = @business_date)
			begin
				insert into md_dayend_control_table values(@branch_code, @business_date, '')
			end
			goto chk_rec_count_error
		end
	end
end
--Multiple Business Date Checking-----------------------------------


--Carl
--insert into convert_log select @business_date, a.brno, 0, poll_method = case when upper(a.pword) in( 'LINUX' ) then '2' when upper(a.pword) in('SQL2000', 'ASO','UCR') 
--	then '1' end, null, ' ', user, getdate() from maximdb.dbo.branch a (nolock), poll_branch_info b (nolock) where a.brno = b.bran_code and 
--	a.brno = @branch_code and a.open_date <= @business_date and a.brno not in (select brno from convert_log (nolock) where ttdate = @business_date)


if @bal_controller = 'YES'
	insert into convert_pool select @business_date, a.brno, poll_method = case when upper(a.pword) = 'LINUX' then '2' when upper(a.pword) in ('SQL2000', 'ASO', 'UCR') then '1' else '1' end, 
		null, ' ', user, getdate() from maximdb.dbo.branch a (nolock), poll_branch_info b (nolock) where a.brno = b.bran_code and a.brno = @branch_code and 
		a.open_date <= @business_date and a.brno not in (select brno from convert_pool (nolock) where ttdate = @business_date) and
		a.brno not in (select brno from convert_log (nolock) where ttdate = @business_date) 
else
--Carl
	insert into convert_log select @business_date, a.brno, 0, poll_method = case when upper(a.pword) = 'LINUX' then '2' when upper(a.pword) in ('SQL2000', 'ASO', 'UCR') then '1' else '1' end, 
		null, ' ', user + ' - ' + @@servername, getdate() from maximdb.dbo.branch a (nolock), poll_branch_info b (nolock) where a.brno = b.bran_code and a.brno = @branch_code and 
		a.open_date <= @business_date and a.brno not in (select brno from convert_log (nolock) where ttdate = @business_date)



update hist_possystem set status = 'C' where business_date = @business_date and branch_code = @branch_code
update poll_scheme_control_table set status = 'C' where business_date = @business_date and branch_code = @branch_code and poll_scheme = 'HT'

chk_rec_count_error:
close chk_table_cursor
deallocate chk_table_cursor

return


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO