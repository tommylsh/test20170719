CREATE  PROCEDURE [dbo].[udsp_convert_voidsaleitem]

@brhcode	char(6),
@ttdate		char(8),
@runno		int,
@runtype	bit

AS

SET NOCOUNT ON


if @runtype = 0 begin transaction

declare @brid	varchar(6)

select @brid = brid from maximdb.dbo.branch (nolock) where brno = @brhcode

--	select @brid, a.business_date, a.order_no, a.order_seq, a.event_no, a.item_seq, a.subitem_seq, a.item_code, 
	select /*@brid*/ @brid as branch_code,  business_date,order_no, order_seq, left(event_no, 10) as event_no, item_seq, subitem_seq, item_code, 	
		dept_code =
			case
				when trans_type = '00'				then 294
				when trans_type between '40' and '41'	then 8402
				else dept_code
			end,
--		item_cdesc,
  CONVERT(VARCHAR(21),item_cdesc) as item_cdesc ,
 unit_price as list_price, unit_price as price, 
--item_qty = case when a.refund = '1' then a.item_qty * -1 else a.item_qty end,
  item_qty = case   when void = 0 	then item_qty 
		            when void = 1 	then item_qty  * -1
		             when trans_type = '00' 	then 1
       	end, 
		itemtot = case when refund = '1' then (cast(unit_price as money) * item_qty) * -1 else (cast(unit_price as money) * item_qty) end, service_chg, set_menu_code, set_menu, pmt_no, 
		disc_off_rate = case when refund = '1' then disc_off_rate * -1 else disc_off_rate end, 
		deduct_amt = case when refund = '1' then deduct_amt * -1 else deduct_amt end, 
		modif_amt = case when refund = '1' then modif_amt * -1 else modif_amt end, 0 as cost, ' ' as tax_code, 0 as tax_amt, trans_type as order_type, trans_type, 
		station_id, trans_datetime, cast(uid as varchar) as  input_id , refund as void_flag, 
		void_date_time = case when refund = '0' and void = '0' then '' when refund = '1' and void = '0' then trans_datetime else isnull(void_datetime, '') end, 
		void_id = case when refund = '0' and void = '0' then '' when refund = '1' and void = '0' then cast(uid as varchar) else isnull(cast(void_uid as varchar), '') end,
		void_reason, ' ' as remarks01, remarks02 = case when refund = '1' then isnull(cast(auth_uid as varchar), '') else '' end, 
		remarks03 = take_place /*, 'POS' as data_source, user_name() as last_update_user, getdate() as last_update_time, host_name() as workstation_name, 'JOB_Schedule' as app_name,  @runno as runno, status, rowguid*/
	into #voidtrans  from hist_voidtrans (nolock) 
where branch_code = @brhcode and business_date = @ttdate  and item_code  <>  '' and recall = 0 
--and branch_code not in ('5001','5002','5118','5172','5143') 
and branch_code not in (select bran_code  from poll_branch_scheme ( nolock) where poll_scheme='HD' and poll_grp=12)  


--insert into  it19.sales.dbo.voiditem 
insert into daily_voiditem
select   branch_code ,  convert(char(8), business_date, 112) as business_date ,/*left(trans_datetime,4)*/
convert(int, replace(convert(char(5),trans_datetime ,108) ,':','')) as  trans_datetime ,cast(input_id as int)  as  input_id,order_no,
item_code ,dept_code ,item_cdesc,list_price,item_qty,itemtot,service_chg,set_menu,'' as trantype,void_reason,  convert(int, replace(convert(char(5),void_date_time ,108) ,':','')) as   void_datetime  , 
		 isnull(cast(void_id as varchar), '')  as  voiddesc ,	 ' ' as  tsfdesc ,@runno  
 From  #voidtrans

if @@ERROR <> 0 goto error_all


if @runtype = 0 
begin
	if @@trancount > 0
		commit transaction
end

drop table #voidtrans

return 1


error_all:
if @runtype = 0 rollback transaction
drop table #voidtrans

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

