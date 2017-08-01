CREATE  procedure [dbo].[udsp_convert_update_by_batch_IT12]
@runtype		bit, 
@process_type	char(1) = 'M'
AS

declare @tempbranch	char(6)
declare @ttdate		datetime
declare @r			int
declare @xttdate	char(9)
--C 23/11
declare @tempbrno	char(6)
--C 23/11

SET XACT_ABORT ON

SET NOCOUNT ON

if exists (select top 1 * from daily_sales_order (nolock)) goto start_update
if exists (select top 1 * from daily_sales_item (nolock)) goto start_update
if exists (select top 1 * from daily_sales_pay (nolock)) goto start_update
if exists (select top 1 * from daily_coupon_order (nolock)) goto start_update
if exists (select top 1 * from daily_coupon_tran (nolock)) goto start_update
if exists (select top 1 * from daily_sales_supp (nolock)) goto start_update
if exists (select top 1 * from daily_sales_payfig (nolock)) goto start_update
if exists (select top 1 * from daily_item_move (nolock)) goto start_update
if exists (select top 1 * from daily_vip_reference (nolock)) goto start_update
if exists (select top 1 * from daily_bankin (nolock)) goto start_update
if exists (select top 1 * from daily_trans_ecard (nolock)) goto start_update

return 0

start_update:

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
if @runtype = 0 begin distributed transaction

declare branchcursor CURSOR
	for 
	select distinct brid, ttdate from daily_item_move

open branchcursor

fetch branchcursor into @tempbranch, @ttdate

while (@@FETCH_STATUS <> -1)
	begin
		select @xttdate = convert(char(9), @ttdate, 112)
--Delete IT18 Item Move
		execute @r = it18.iis_bky.dbo.udsp_delete_item_move @tempbranch, @xttdate
--Delete IT18 Item Move
		fetch branchcursor into @tempbranch, @ttdate
	end

close branchcursor
deallocate branchcursor

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
--Insert Item Move into IT18
insert into it18.iis_bky.dbo.item_move select * from daily_item_move(nolock)
--Insert Item Move into IT18

insert into it12.sales.dbo.runlog select runno, getdate(), brid, 'citem', count(*), 0, 
	convert(char(9), ttdate, 112) + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_item_move(nolock) group by runno, brid, ttdate
if @@ERROR <> 0 goto error_all


--C 23/11

select @tempbrno = brno from maximdb.dbo.branch (nolock) where brid = (select top 1 brid from daily_item_move(nolock))

select top 1 @tempbranch = brid, @xttdate = convert(char(8), ttdate, 112) from daily_item_move (nolock)

if 	exists (select * from hist_possystem (nolock) where post_mode&16 = 16 and business_date = @xttdate and branch_code = @tempbrno)
	begin
		if exists(select top 1 * from hist_itemstock (nolock) where business_date = @xttdate and branch_code = @tempbrno)
			begin
				execute it18.iis_bky.dbo.udsp_item_move_simulation_vs_actual @xttdate, @tempbranch
			end
	end

--C 23/11

if @runtype = 0 commit transaction
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


if @runtype = 0 begin distributed transaction

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
/*
insert into it12.sales.dbo.chklog select * from daily_chklog(nolock)
if @@ERROR <> 0 goto error_all

insert into it12.sales.dbo.runlog select runno, getdate(), brid, 'chklog', count(*), 0, 
	convert(char(9), post_date, 112) + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_chklog(nolock) group by runno, brid, post_date
if @@ERROR <> 0 goto error_all
*/
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
insert into it12.sales.dbo.coupon_order select * from daily_coupon_order(nolock)
if @@ERROR <> 0 goto error_all

insert into it12.sales.dbo.runlog select runno, getdate(), brid, 'cord', count(*), sum(total_amount), 
	cast(ttdate as char(9)) + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_coupon_order(nolock) group by runno, brid, ttdate
if @@ERROR <> 0 goto error_all

insert into it12.sales.dbo.coupon_tran select * from daily_coupon_tran(nolock)
if @@ERROR <> 0 goto error_all

insert into it12.sales.dbo.runlog select runno, getdate(), brid, 'ctrn', count(*), 0, 
	cast(ttdate as char(9)) + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_coupon_tran(nolock) group by runno, brid, ttdate
if @@ERROR <> 0 goto error_all

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
/*
insert into it12.sales.dbo.damage select * from daily_damage(nolock)
if @@ERROR <> 0 goto error_all

insert into it12.sales.dbo.runlog select runno, getdate(), brid, 'damage', count(*), sum(itemtot), 
	cast(ttdate as char(9)) + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_damage(nolock) group by runno, brid, ttdate
if @@ERROR <> 0 goto error_all
*/
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
/*
declare branchcursor CURSOR
	for 
	select distinct brid, ttdate from daily_item_move

open branchcursor

fetch branchcursor into @tempbranch, @ttdate

while (@@FETCH_STATUS <> -1)
	begin
		select @xttdate = convert(char(9), @ttdate, 112)
--Delete IT18 Item Move
		execute @r = it18.iis_bky.dbo.udsp_delete_item_move @tempbranch, @xttdate
--Delete IT18 Item Move
		fetch branchcursor into @tempbranch, @ttdate
	end

close branchcursor
deallocate branchcursor

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
--Insert Item Move into IT18
insert into it18.iis_bky.dbo.item_move select * from daily_item_move(nolock)
--Insert Item Move into IT18

insert into it12.sales.dbo.runlog select runno, getdate(), brid, 'citem', count(*), 0, 
	convert(char(9), ttdate, 112) + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_item_move(nolock) group by runno, brid, ttdate
if @@ERROR <> 0 goto error_all

--C 23/11

select @tempbrno = brno from maximdb.dbo.branch (nolock) where brid = (select top 1 brid from daily_item_move(nolock))

select top 1 @tempbranch = brid, @xttdate = convert(char(8), ttdate, 112) from daily_item_move (nolock)

if 	exists (select * from hist_possystem (nolock) where post_mode&16 = 16 and business_date = @xttdate and branch_code = @tempbrno)
	begin
		if exists(select top 1 * from hist_itemstock (nolock) where business_date = @xttdate and branch_code = @tempbrno)
			begin
				execute it18.iis_bky.dbo.udsp_item_move_simulation_vs_actual @xttdate, @tempbranch
			end
	end

--C 23/11
*/
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
insert into it12.sales.dbo.sales_payfig select * from daily_sales_payfig
if @@ERROR <> 0 goto error_all

insert into it12.sales.dbo.runlog select runno, getdate(), bran_code, 'payfig', count(*), sum(total_amount), 
	convert(char(8), business_date, 112) + ' Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_sales_payfig(nolock) 
	group by runno, bran_code, business_date
if @@ERROR <> 0 goto error_all
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
--insert into it12.sales.dbo.recall select * from daily_recall(nolock)
--if @@ERROR <> 0 goto error_all

--insert into it12.sales.dbo.runlog select runno, getdate(), brid, 'recall', count(*), sum(itemtot), 
	--cast(ttdate as char(9)) + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_recall(nolock) group by runno, brid, ttdate
--if @@ERROR <> 0 goto error_all

--insert into it12.sales.dbo.recall_salepay select * from daily_recall_salepay(nolock)
--if @@ERROR <> 0 goto error_all

--insert into it12.sales.dbo.runlog select runno, getdate(), brid, 'recpay', count(*), sum(payamt), 
--	cast(ttdate as char(9)) + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_recall_salepay(nolock) group by runno, brid, ttdate
--if @@ERROR <> 0 goto error_all
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
insert into view_insert_it12sale_item select * From daily_sales_item
if @@ERROR <> 0 goto error_all


insert into it12.sales.dbo.runlog select runno, getdate(), bran_code, 'trans', count(*), sum(itemtot), 
	convert(char(8), business_date, 112) + ' Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_sales_item(nolock) 
	group by runno, bran_code, business_date
if @@ERROR <> 0 goto error_all
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
insert into view_insert_it12sale_order select * from daily_sales_order
if @@ERROR <> 0 goto error_all

insert into it12.sales.dbo.runlog select runno, getdate(), bran_code, 'ords', count(*), sum(foodamt), 
	convert(char(8), business_date, 112) + ' Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_sales_order (nolock) 
	group by runno, bran_code, business_date
if @@ERROR <> 0 goto error_all
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
insert into view_insert_it12payment select * from daily_sales_pay
if @@ERROR <> 0 goto error_all

insert into it12.sales.dbo.runlog select runno, getdate(), bran_code, 'salpay', count(*), sum(local_amount), 
	convert(char(8), business_date, 112) + ' Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_sales_pay(nolock) 
	group by runno, bran_code, business_date
if @@ERROR <> 0 goto error_all
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

insert into it12.sales.dbo.voidord select * from daily_voidord(nolock)
if @@ERROR <> 0 goto error_all

insert into it12.sales.dbo.runlog select runno, getdate(), brid, 'voidord', count(*), sum(foodamt), 
	cast(ttdate as char(9)) + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_voidord(nolock) group by runno, brid, ttdate
if @@ERROR <> 0 goto error_all
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
insert into it12.sales.dbo.voiditem select * from daily_voiditem(nolock)
if @@ERROR <> 0 goto error_all

insert into it12.sales.dbo.runlog select runno, getdate(), brid, 'voiditem', count(*), sum(itemtot), 
	cast(ttdate as char(9)) + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_voiditem(nolock) group by runno, brid, ttdate
if @@ERROR <> 0 goto error_all

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
insert into it12.sales.dbo.sales_supp select * from daily_sales_supp(nolock)
if @@ERROR <> 0 goto error_all


insert into it12.sales.dbo.runlog select runno, getdate(), branch_code, 'supp', count(*), 0, 
	convert(char(8), business_date, 112) + ' Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_sales_supp(nolock) 
	group by runno, branch_code, business_date
if @@ERROR <> 0 goto error_all
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
insert into it12.sales.dbo.vip_reference select * from daily_vip_reference(nolock)
if @@ERROR <> 0 goto error_all

insert into it12.sales.dbo.runlog select runno, getdate(), brid, 'vip', count(*), sum(foodamt), 
	cast(ttdate as char(9))  + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_vip_reference(nolock) group by runno, brid, ttdate
if @@ERROR <> 0 goto error_all
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



insert into it12.sales.dbo.trans_ecard select * from daily_trans_ecard(nolock)
if @@ERROR <> 0 goto error_all


insert into it12.sales.dbo.runlog select runno, getdate(), branch_code, 'ecard', count(*), 0, 
	convert(char(8), business_date, 112) + ' Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_trans_ecard (nolock) 
	group by runno, branch_code, business_date
if @@ERROR <> 0 goto error_all



------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



declare branchcursor CURSOR
	for 
	select distinct bran_code, business_date from daily_sales_addition_detail

open branchcursor

fetch branchcursor into @tempbranch, @ttdate

while (@@FETCH_STATUS <> -1)
	begin
		select @xttdate = convert(char(9), @ttdate, 112)
		execute @r = it12.sales.dbo.udsp_delete_daily_sales_addition_detail @tempbranch, @xttdate
		fetch branchcursor into @tempbranch, @ttdate
	end

close branchcursor
deallocate branchcursor


insert into it12.sales.dbo.daily_sales_addition_detail select * from daily_sales_addition_detail (nolock)
if @@ERROR <> 0 goto error_all
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
declare branchcursor CURSOR
	for 
	select distinct bran_code, hist_date from daily_bankin (nolock)

open branchcursor

fetch branchcursor into @tempbranch, @ttdate

while (@@FETCH_STATUS <> -1)
	begin
		execute it12.pos_bankin.dbo.udsp_delete_bank_in @tempbranch, @ttdate
		fetch branchcursor into @tempbranch, @ttdate
	end

close branchcursor
deallocate branchcursor

insert into it12.pos_bankin.dbo.hist_payfig select * from daily_bankin (nolock)
if @@ERROR <> 0 goto error_all
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
/*
----- Update IT19 Daily Balance ----- 
declare branchcursor CURSOR
	for 
	select distinct brid, cast(ttdate as char(8)) from daily_saleord

open branchcursor

fetch branchcursor into @tempbranch, @xttdate

while (@@FETCH_STATUS <> -1)
	begin

		execute @r = it19.sales.dbo.udsp_gen_daily_balance @xttdate, @tempbranch
		if (@@ERROR <> 0) or (@r = 0)  goto error_all

		execute @r = it12.sales.dbo.udsp_gen_ytd @xttdate, @tempbranch
		if (@@ERROR <> 0) or (@r = 0)  goto error_all

		fetch branchcursor into @tempbranch, @xttdate
	end

close branchcursor
deallocate branchcursor
----- Update IT19 Daily Balance ----- 
*/
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
update convert_log set conv_flag = 'C', conv_date = getdate() from convert_log a(nolock), (select distinct business_date, bran_code from daily_sales_order) b where 
	convert(char(8), a.ttdate, 112) = convert(char(8), b.business_date, 112) and 'm' + a.brno = b.bran_code

/*
--delete from daily_chklog
--delete from daily_damage
--delete from daily_onhouse
--delete from daily_payfig
--delete from daily_recall
--delete from daily_recall_salepay
*/
delete from daily_voiditem
delete from daily_voidord


delete from daily_sales_order
delete from daily_sales_item
delete from daily_sales_pay
delete from daily_coupon_order
delete from daily_coupon_tran
delete from daily_sales_supp
delete from daily_sales_payfig
delete from daily_item_move
delete from daily_vip_reference
delete from daily_bankin
delete from daily_sales_addition_detail
delete from daily_trans_ecard


if @runtype = 0 commit transaction
return 1

error_all:
if @runtype = 0 
	begin
		if @@trancount > 0 
			begin
				rollback transaction
				if upper(@process_type) = 'A'
					update convert_log set conv_flag = '' from convert_log a(nolock), (select distinct business_date, bran_code from daily_sales_order) b where 
						convert(char(8), a.ttdate, 112) = convert(char(8), b.business_date, 112) and 'm' + a.brno = b.bran_code
			end
	end

return 0




GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO



CREATE  procedure [dbo].[udsp_convert_update_by_batch_IT13]
@runtype		bit, 
@process_type	char(1) = 'M'
AS

declare @tempbranch	char(6)
declare @ttdate		datetime
declare @r			int
declare @xttdate	char(9)
--C 23/11
declare @tempbrno	char(6)
--C 23/11

SET XACT_ABORT ON

SET NOCOUNT ON

if exists (select top 1 * from daily_sales_order (nolock)) goto start_update
if exists (select top 1 * from daily_sales_item (nolock)) goto start_update
if exists (select top 1 * from daily_sales_pay (nolock)) goto start_update
if exists (select top 1 * from daily_coupon_order (nolock)) goto start_update
if exists (select top 1 * from daily_coupon_tran (nolock)) goto start_update
if exists (select top 1 * from daily_sales_supp (nolock)) goto start_update
if exists (select top 1 * from daily_sales_payfig (nolock)) goto start_update
if exists (select top 1 * from daily_item_move (nolock)) goto start_update
if exists (select top 1 * from daily_vip_reference (nolock)) goto start_update
if exists (select top 1 * from daily_bankin (nolock)) goto start_update
if exists (select top 1 * from daily_trans_ecard (nolock)) goto start_update


return 0

start_update:


------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
if @runtype = 0 begin distributed transaction

declare branchcursor CURSOR
	for 
	select distinct brid, ttdate from daily_item_move

open branchcursor

fetch branchcursor into @tempbranch, @ttdate

while (@@FETCH_STATUS <> -1)
	begin
		select @xttdate = convert(char(9), @ttdate, 112)
--Delete IT18 Item Move
		execute @r = it18.iis_bky.dbo.udsp_delete_item_move @tempbranch, @xttdate
--Delete IT18 Item Move
		fetch branchcursor into @tempbranch, @ttdate
	end

close branchcursor
deallocate branchcursor

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
--Insert Item Move into IT18
insert into it18.iis_bky.dbo.item_move select * from daily_item_move(nolock)
--Insert Item Move into IT18

insert into it13.sales.dbo.ff_runlog select runno, getdate(), brid, 'citem', count(*), 0, 
	convert(char(9), ttdate, 112) + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_item_move(nolock) group by runno, brid, ttdate
if @@ERROR <> 0 goto error_all


--C 23/11

select @tempbrno = brno from maximdb.dbo.branch (nolock) where brid = (select top 1 brid from daily_item_move(nolock))

select top 1 @tempbranch = brid, @xttdate = convert(char(8), ttdate, 112) from daily_item_move (nolock)

if 	exists (select * from hist_possystem (nolock) where post_mode&16 = 16 and business_date = @xttdate and branch_code = @tempbrno)
	begin
		if exists(select top 1 * from hist_itemstock (nolock) where business_date = @xttdate and branch_code = @tempbrno)
			begin
				execute it18.iis_bky.dbo.udsp_item_move_simulation_vs_actual @xttdate, @tempbranch
			end
	end

--C 23/11

if @runtype = 0 commit transaction
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


if @runtype = 0 begin distributed transaction

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
/*
insert into it13.sales.dbo.chklog select * from daily_chklog(nolock)
if @@ERROR <> 0 goto error_all

insert into it13.sales.dbo.ff_runlog select runno, getdate(), brid, 'chklog', count(*), 0, 
	convert(char(9), post_date, 112) + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_chklog(nolock) group by runno, brid, post_date
if @@ERROR <> 0 goto error_all
*/
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
insert into it13.sales.dbo.coupon_order select * from daily_coupon_order(nolock)
if @@ERROR <> 0 goto error_all

insert into it13.sales.dbo.ff_runlog select runno, getdate(), brid, 'cord', count(*), sum(total_amount), 
	cast(ttdate as char(9)) + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_coupon_order(nolock) group by runno, brid, ttdate
if @@ERROR <> 0 goto error_all

insert into it13.sales.dbo.coupon_tran select * from daily_coupon_tran(nolock)
if @@ERROR <> 0 goto error_all

insert into it13.sales.dbo.ff_runlog select runno, getdate(), brid, 'ctrn', count(*), 0, 
	cast(ttdate as char(9)) + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_coupon_tran(nolock) group by runno, brid, ttdate
if @@ERROR <> 0 goto error_all

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
/*
insert into it13.sales.dbo.damage select * from daily_damage(nolock)
if @@ERROR <> 0 goto error_all

insert into it13.sales.dbo.ff_runlog select runno, getdate(), brid, 'damage', count(*), sum(itemtot), 
	cast(ttdate as char(9)) + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_damage(nolock) group by runno, brid, ttdate
if @@ERROR <> 0 goto error_all
*/
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
/*
declare branchcursor CURSOR
	for 
	select distinct brid, ttdate from daily_item_move

open branchcursor

fetch branchcursor into @tempbranch, @ttdate

while (@@FETCH_STATUS <> -1)
	begin
		select @xttdate = convert(char(9), @ttdate, 112)
--Delete it18 Item Move
		execute @r = it18.IIS_BKY.dbo.udsp_delete_item_move @tempbranch, @xttdate
--Delete IT18 Item Move
		fetch branchcursor into @tempbranch, @ttdate	end

close branchcursor
deallocate branchcursor

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
--Insert Item Move into IT18
insert into it18.IIS_BKY.dbo.item_move select * from daily_item_move(nolock)
--Insert Item Move into IT18

--insert into it_iis02.iis_bky.dbo.item_move select * from daily_item_move(nolock)

insert into it13.sales.dbo.ff_runlog select runno, getdate(), brid, 'citem', count(*), 0, 
	convert(char(9), ttdate, 112) + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_item_move(nolock) group by runno, brid, ttdate
if @@ERROR <> 0 goto error_all

--C 23/11

select @tempbrno = brno from maximdb.dbo.branch (nolock) where brid = (select top 1 brid from daily_item_move(nolock))

select top 1 @tempbranch = brid, @xttdate = convert(char(8), ttdate, 112) from daily_item_move (nolock)

if 	exists (select * from hist_possystem (nolock) where post_mode&16 = 16 and business_date = @xttdate and branch_code = @tempbrno)
	begin
		if exists(select top 1 * from hist_itemstock (nolock) where business_date = @xttdate and branch_code = @tempbrno)
			begin
				execute it18.iis_bky.dbo.udsp_item_move_simulation_vs_actual @xttdate, @tempbranch
			end
	end

--C 23/11
*/
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
insert into it13.sales.dbo.sales_payfig select * from daily_sales_payfig
if @@ERROR <> 0 goto error_all

insert into it13.sales.dbo.ff_runlog select runno, getdate(), bran_code, 'payfig', count(*), sum(total_amount), 
	convert(char(8), business_date, 112) + ' Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_sales_payfig(nolock) 
	group by runno, bran_code, business_date
if @@ERROR <> 0 goto error_all
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
--insert into view_insert_it13sale_order select * from view_daily_onhouse
--if @@ERROR <> 0 goto error_all
--
--insert into it13.sales.dbo.ff_runlog select runno, getdate(), brid, 'onhouse', count(*), sum(foodamt), 
--	cast(ttdate as char(9)) + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_onhouse(nolock) group by runno, brid, ttdate
--if @@ERROR <> 0 goto error_all
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
--insert into it13.sales.dbo.recall select * from daily_recall(nolock)
--if @@ERROR <> 0 goto error_all

--insert into it13.sales.dbo.ff_runlog select runno, getdate(), brid, 'recall', count(*), sum(itemtot), 
	--cast(ttdate as char(9)) + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_recall(nolock) group by runno, brid, ttdate
--if @@ERROR <> 0 goto error_all

--insert into it13.sales.dbo.recall_salepay select * from daily_recall_salepay(nolock)
--if @@ERROR <> 0 goto error_all

--insert into it13.sales.dbo.ff_runlog select runno, getdate(), brid, 'recpay', count(*), sum(payamt), 
--	cast(ttdate as char(9)) + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_recall_salepay(nolock) group by runno, brid, ttdate
--if @@ERROR <> 0 goto error_all
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
insert into view_insert_it13sale_item select * From daily_sales_item
if @@ERROR <> 0 goto error_all

insert into it13.sales.dbo.ff_runlog select runno, getdate(), bran_code, 'trans', count(*), sum(itemtot), 
	convert(char(8), business_date, 112) + ' Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_sales_item(nolock) 
	group by runno, bran_code, business_date
if @@ERROR <> 0 goto error_all
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
insert into view_insert_it13sale_order select * from daily_sales_order
if @@ERROR <> 0 goto error_all

insert into it13.sales.dbo.ff_runlog select runno, getdate(), bran_code, 'ords', count(*), sum(foodamt), 
	convert(char(8), business_date, 112) + ' Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_sales_order (nolock) 
	group by runno, bran_code, business_date
if @@ERROR <> 0 goto error_all
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
insert into view_insert_it13payment select * from daily_sales_pay
if @@ERROR <> 0 goto error_all

insert into it13.sales.dbo.ff_runlog select runno, getdate(), bran_code, 'salpay', count(*), sum(local_amount), 
	convert(char(8), business_date, 112) + ' Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_sales_pay(nolock) 
	group by runno, bran_code, business_date
if @@ERROR <> 0 goto error_all
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

insert into it13.sales.dbo.voidord select * from daily_voidord(nolock)
if @@ERROR <> 0 goto error_all

insert into it13.sales.dbo.ff_runlog select runno, getdate(), brid, 'voidord', count(*), sum(foodamt), 
	cast(ttdate as char(9)) + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_voidord(nolock) group by runno, brid, ttdate
if @@ERROR <> 0 goto error_all
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
insert into it13.sales.dbo.voiditem select * from daily_voiditem(nolock)
if @@ERROR <> 0 goto error_all

insert into it13.sales.dbo.ff_runlog select runno, getdate(), brid, 'voiditem', count(*), sum(itemtot), 
	cast(ttdate as char(9)) + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_voiditem(nolock) group by runno, brid, ttdate
if @@ERROR <> 0 goto error_all

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
insert into it13.sales.dbo.sales_supp select * from daily_sales_supp(nolock)
if @@ERROR <> 0 goto error_all

insert into it13.sales.dbo.ff_runlog select runno, getdate(), branch_code, 'supp', count(*), 0, 
	convert(char(8), business_date, 112) + ' Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_sales_supp(nolock) 
	group by runno, branch_code, business_date
if @@ERROR <> 0 goto error_all
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
insert into it13.sales.dbo.vip_reference select * from daily_vip_reference(nolock)
if @@ERROR <> 0 goto error_all

insert into it13.sales.dbo.ff_runlog select runno, getdate(), brid, 'vip', count(*), sum(foodamt), 
	cast(ttdate as char(9))  + 'Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_vip_reference(nolock) group by runno, brid, ttdate
if @@ERROR <> 0 goto error_all
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


insert into it13.sales.dbo.trans_ecard select * from daily_trans_ecard(nolock)
if @@ERROR <> 0 goto error_all


insert into it13.sales.dbo.ff_runlog select runno, getdate(), branch_code, 'ecard', count(*), 0, 
	convert(char(8), business_date, 112) + ' Process On ' + convert(char(3),(day(getdate()))) + 'System Gen.' from daily_trans_ecard(nolock) 
	group by runno, branch_code, business_date
if @@ERROR <> 0 goto error_all



------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


declare branchcursor CURSOR
	for 
	select distinct bran_code, business_date from daily_sales_addition_detail

open branchcursor

fetch branchcursor into @tempbranch, @ttdate

while (@@FETCH_STATUS <> -1)
	begin
		select @xttdate = convert(char(9), @ttdate, 112)
		execute @r = it13.sales.dbo.udsp_delete_daily_sales_addition_detail @tempbranch, @xttdate
		fetch branchcursor into @tempbranch, @ttdate	end

close branchcursor
deallocate branchcursor


insert into it13.sales.dbo.daily_sales_addition_detail select * from daily_sales_addition_detail (nolock)
if @@ERROR <> 0 goto error_all
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
declare branchcursor CURSOR
	for 
	select distinct bran_code, hist_date from daily_bankin (nolock)

open branchcursor

fetch branchcursor into @tempbranch, @ttdate

while (@@FETCH_STATUS <> -1)
	begin
		execute it12.pos_bankin.dbo.udsp_delete_bank_in @tempbranch, @ttdate
		fetch branchcursor into @tempbranch, @ttdate	end

close branchcursor
deallocate branchcursor

insert into it12.pos_bankin.dbo.hist_payfig select * from daily_bankin (nolock)
if @@ERROR <> 0 goto error_all
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
/*
----- Update IT19 Daily Balance ----- 
declare branchcursor CURSOR
	for 
	select distinct brid, cast(ttdate as char(8)) from daily_saleord

open branchcursor

fetch branchcursor into @tempbranch, @xttdate

while (@@FETCH_STATUS <> -1)
	begin

		execute @r = it19.sales.dbo.udsp_gen_daily_balance @xttdate, @tempbranch
		if (@@ERROR <> 0) or (@r = 0)  goto error_all

		execute @r = it13.sales.dbo.udsp_gen_ytd @xttdate, @tempbranch
		if (@@ERROR <> 0) or (@r = 0)  goto error_all

		fetch branchcursor into @tempbranch, @xttdate	end

close branchcursor
deallocate branchcursor
----- Update IT19 Daily Balance ----- 
*/
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
update convert_log set conv_flag = 'C', conv_date = getdate() from convert_log a(nolock), (select distinct business_date, bran_code from daily_sales_order) b where 
	convert(char(8), a.ttdate, 112) = convert(char(8), b.business_date, 112) and 'm' + a.brno = b.bran_code

/*
--delete from daily_chklog
--delete from daily_damage
--delete from daily_onhouse
--delete from daily_payfig
--delete from daily_recall
--delete from daily_recall_salepay
*/
delete from daily_voiditem
delete from daily_voidord


delete from daily_sales_order
delete from daily_sales_item
delete from daily_sales_pay
delete from daily_coupon_order
delete from daily_coupon_tran
delete from daily_sales_supp
delete from daily_sales_payfig
delete from daily_item_move
delete from daily_vip_reference
delete from daily_bankin
delete from daily_sales_addition_detail
delete from daily_trans_ecard

if @runtype = 0 commit transaction
return 1

error_all:
if @runtype = 0 
	begin
		if @@trancount > 0 
			begin
				rollback transaction
				if upper(@process_type) = 'A'
					update convert_log set conv_flag = '' from convert_log a(nolock), (select distinct business_date, bran_code from daily_sales_order) b where 
						convert(char(8), a.ttdate, 112) = convert(char(8), b.business_date, 112) and 'm' + a.brno = b.bran_code
			end
	end

return 0



GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO
