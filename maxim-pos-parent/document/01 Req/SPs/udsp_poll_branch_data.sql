CREATE  procedure [dbo].[udsp_poll_branch_data]
@brhcode		char(6),
@poll_grp		smallint,
@poll_type	char(2)	= 'N',
@ipoll_scheme	char(3)	= 'ALL',
@max_count	int	= 3

AS

declare @status_flag		char(2)

declare @bran_code		char(6)
declare @poll_scheme	char(2)
declare @poll_count  		smallint
declare @status 		char(2)
declare @direction		smallint
declare @last_update_time	datetime
declare @chk_exists_data	char(1)

declare @client_table         	sysname
declare @host_table           	sysname
declare @host_computer_name	sysname
declare @host_db              	sysname
declare @client_computer_name sysname
declare @client_db            	sysname

declare @temp_bran_code	char(6)
declare @temp_poll_scheme	char(2)
declare @temp_poll_grp	smallint
declare @chk_poll_client	char(20)

declare @host_info		sysname
declare @source_info		sysname
declare @sqlstring		varchar(600)

declare @r			int
declare  @GB char(1)  -- add by hing
SET XACT_ABORT ON

SET NOCOUNT ON

/***********************************************************************************************
@max_count	-----> No. of retry
@status_flag	-----> Poll client data where status = @status_flag
@poll_type	-----> N (Normal), M (Manual) Polling
direction		-----> 1 (Up), 2 (Down)
status		-----> I (InProgress), P (Processing) E (Error), C (Completed)
enable		-----> 0 (Disable), 1 (Enable), 2 (Enable - between start & end time)
************************************************************************************************/

select @status_flag = ''

select @temp_bran_code = ''
select @temp_poll_scheme = ''
select @temp_poll_grp = 0


select * into #xx from view_poll_info1(nolock) where 1 = 2

if upper(@poll_type) = 'N'
	begin
		insert into #xx select * from view_poll_info1(nolock) where poll_grp = @poll_grp and direction > 0 and enable in ('1', '2') and status not in ('C', 'E') and sp_status = '' order by poll_seq, bran_code

		update #xx set end_time = '23:59:59' where start_time > end_time and convert(char(8), getdate(), 108) < '23:59:59'  and convert(char(8), getdate(), 108)  > start_time and enable = '2'
		update #xx set start_time = '00:00:01' where start_time > end_time and convert(char(8), getdate(), 108) < '23:59:59'  and convert(char(8), getdate(), 108)  < start_time and enable = '2'

		delete from #xx where  enable = '2' and convert(char(8), getdate(), 108) not between start_time and end_time
	end
else
	if upper(@poll_type) = 'M'
		begin
			if upper(@ipoll_scheme) = 'ALL'
				insert into #xx select * from view_poll_info1(nolock) where bran_code = @brhcode and poll_scheme not in ('OP')
			else
				insert into #xx select * from view_poll_info1(nolock) where bran_code = @brhcode and poll_scheme = upper(@ipoll_scheme)
		end


select * into #poll_info from #xx where 1 = 2

if upper(@brhcode) = 'ALL'
	insert into #poll_info select * from #xx(nolock) order by poll_seq, bran_code, poll_grp
else
	insert into #poll_info select * from #xx(nolock) where bran_code = @brhcode order by poll_seq, bran_code, poll_grp


declare poll_info_cursor CURSOR
	for 
	select bran_code, poll_grp, poll_scheme, poll_count, status, direction, last_update_time, chk_exists_data, client_table, host_table, host_computer_name, 
		host_db, client_computer_name, client_db from #poll_info(nolock)

open poll_info_cursor

fetch poll_info_cursor into 
	@bran_code, @poll_grp, @poll_scheme, @poll_count, @status, @direction, @last_update_time, @chk_exists_data, @client_table, @host_table, @host_computer_name, 
	@host_db, @client_computer_name, @client_db

while (@@FETCH_STATUS <> -1)
	begin
		if (@temp_bran_code <> @bran_code or @temp_poll_scheme <> @poll_scheme)
			begin
				if @temp_bran_code <> @bran_code
				begin
					select @GB=tchinese from mitpos_branch (nolock) where branch_code=@brhcode
					
					if @GB<>'T'
					  set @GB='1'
					else
					  set @GB='0'
				end

				select @temp_bran_code = @bran_code, @temp_poll_scheme = @poll_scheme, @temp_poll_grp = @poll_grp

				if (select count(*) from poll_log(nolock) where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme) = 0
					insert into poll_log values (@bran_code, @poll_grp, @poll_scheme, '', '', getdate())


				update poll_branch_scheme with(rowlock) set status = 
									case 
										when (upper(@poll_type) = 'N' and poll_count < @max_count) or status = 'C' then 'I'
										when upper(@poll_type) = 'N' and poll_count >= @max_count and status <> 'C' then 'E'
										when (upper(@poll_type) = 'M' and poll_count < @max_count) or status = 'C' then 'I'
										when upper(@poll_type) = 'M' and poll_count >= @max_count and status <> 'C' then 'E'
										when upper(@poll_type) = 'R' and poll_count >= @max_count and status <> 'C' then 'E'
									end,
					poll_count = poll_count + 1, sp_status =
									case
										when upper(@poll_type) = 'M' then 'M'
										else ''
									end,
					last_update_time = getdate() where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme

				if (select status from poll_branch_scheme (nolock) where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme) = 'E'
					begin
						update poll_log with(rowlock) set status = 'E', poll_msg = 'No. of Retry > @max_count !', last_update_time = getdate() where bran_code = @bran_code and poll_grp =  @poll_grp and poll_scheme =  @poll_scheme
						if upper(@poll_type) <> 'M' goto error_all
					end

				select @sqlstring = 'update poll_log with(rowlock) set status = ' + char(39) + 'I' + char(39) + ', poll_msg = (select srvname from ' + rtrim(@client_computer_name) + '.master.dbo.sysservers where srvid = 0), last_update_time = getdate() where bran_code = ' + char(39) + @bran_code + char(39) + ' and poll_grp =  ' + cast(@poll_grp as char(2)) + ' and poll_scheme =  ' + char(39) + @poll_scheme + char(39)
				execute(@sqlstring)
			end

		select @chk_poll_client = poll_msg from poll_log (nolock) where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme

		if (@chk_poll_client = @client_computer_name) 
			begin
				begin distributed transaction poll_data
					while (@temp_bran_code = @bran_code) and (@@FETCH_STATUS <> -1 and @temp_poll_scheme = @poll_scheme)
						begin
							select @host_info = rtrim(@host_computer_name)  +'.' +  rtrim(@host_db) + '.dbo.' + rtrim(@host_table),
								@source_info = rtrim(@client_computer_name) + '.' + rtrim(@client_db) + '.dbo.x' + rtrim(@client_table)

							begin transaction update_log
								update poll_log with(rowlock) set status = 'I', poll_msg = 'Begin Poll Table - ' + rtrim(@client_table) + ' !', last_update_time = getdate() where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme
							commit transaction update_log

							select @sqlstring = rtrim(@client_computer_name) + '.' + rtrim(@client_db) + '.dbo.udsp_polling_step1 '
							execute @r = @sqlstring @client_table, @host_info, @bran_code, @direction, @status_flag
							if (@r = 1) 
								begin
									begin transaction update_log
										update poll_log with(rowlock) set status = 'P', poll_msg = 'udsp_polling_step1 Completed !' where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme
									commit transaction update_log
								end

							if ((select status from poll_log (nolock) where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme) <> 'P') 
								begin
									rollback transaction poll_data
									update poll_log with(rowlock) set status = 'E', poll_msg = 'ERROR - udsp_polling_step1 ! ' + rtrim(@client_table), last_update_time = getdate() where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme
									goto error_all
								end

							select @r = 0
							if (@direction = 1)
								begin
									begin transaction update_log
										update poll_log with(rowlock) set status = 'I', poll_msg = 'Begin Update (Host) Data ! - ' + rtrim(@host_table), last_update_time = getdate() where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme
									commit transaction update_log

									if @GB='1'
									begin
										declare @table_name1		sysname
										select @table_name1='##' + replace (@source_info,'.','_')
										
										if not ( select object_id('tempdb..'+ @table_name1 ) )  is null
										   execute( 'drop table '+@table_name1   )
										
										set @sqlstring='select * into '+ @table_name1 + ' from ' +@source_info
										execute(@sqlstring)

										exec udsp_TableToBIG5 @bran_code,'',@table_name1, @client_table,1

										select @sqlstring = rtrim(@@servername) + '.' + rtrim(@host_db) + '.dbo.udsp_polling_update '
										execute @r = @sqlstring  @host_table, @table_name1, @chk_exists_data

										if not ( select object_id('tempdb..'+ @table_name1 ) )  is null
										   execute( 'drop table '+@table_name1   )
									
									end
									else
									begin


--20051017									select @sqlstring = rtrim(@host_computer_name) + '.' + rtrim(@host_db) + '.dbo.udsp_polling_update '
									select @sqlstring = rtrim(@@servername) + '.' + rtrim(@host_db) + '.dbo.udsp_polling_update '
--20051017
									execute @r = @sqlstring  @host_table, @source_info, @chk_exists_data
									end
								end
							else
								begin
									if (@direction = 2)
										begin
											begin transaction update_log
												update poll_log with(rowlock) set status = 'I', poll_msg = 'Begin Update (Client) Data ! - ' + rtrim(@client_table), last_update_time = getdate() where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme
											commit transaction update_log

											select @sqlstring = rtrim(@client_computer_name) + '.' + rtrim(@client_db) + '.dbo.udsp_polling_update '
											execute @r = @sqlstring  @client_table, @source_info
										end
								end

							if (@r = 1) 
								begin
									begin transaction update_log
										update poll_log with(rowlock) set status = 'P', poll_msg = 'udsp_polling_update Completed !' where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme =  @poll_scheme
									commit transaction update_log
								end

							if ((select status from poll_log (nolock) where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme) <> 'P') 
								begin
									rollback transaction poll_data
									update poll_log with(rowlock) set status = 'E', poll_msg = 'ERROR - Update Data ! ' + rtrim(@client_table), last_update_time = getdate() where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme
									goto error_all
								end
							else
/*
							select @r = 0
							if (@direction = 1)
								begin
									begin transaction update_log
										update poll_log with(rowlock) set status = 'I', poll_msg = 'Begin udsp_merge_data ! - ' + rtrim(@host_table), last_update_time = getdate() where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme
									commit transaction update_log

--20051017									select @sqlstring = rtrim(@host_computer_name) + '.' + rtrim(@host_db) + '.dbo.udsp_merge_data ' 
									select @sqlstring = rtrim(@@servername) + '.' + rtrim(@host_db) + '.dbo.udsp_merge_data ' 
--20051017
									execute @r = @sqlstring  @bran_code, @poll_scheme, @host_table
								end

							if (@r = 1) 
								begin
									begin transaction update_log
										update poll_log with(rowlock) set status = 'P', poll_msg = 'udsp_merge_data Completed !' where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme =  @poll_scheme
									commit transaction update_log
								end

							if ((select status from poll_log (nolock) where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme) <> 'P') 
								begin
									rollback transaction poll_data
									update poll_log with(rowlock) set status = 'E', poll_msg = 'ERROR - Merge Data ! ' + rtrim(@host_table), last_update_time = getdate() where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme
									goto error_all
								end
							else
								begin
									begin transaction update_log
										update poll_log with(rowlock) set status = 'I', poll_msg = 'Begin udsp_polling_step2 ! - ' + rtrim(@client_table), last_update_time = getdate() where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme
									commit transaction update_log
								end
*/
							begin transaction update_log
								update poll_log with(rowlock) set status = 'I', poll_msg = 'Begin udsp_polling_step2 ! - ' + rtrim(@client_table), last_update_time = getdate() where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme
							commit transaction update_log

							select @sqlstring = rtrim(@client_computer_name) + '.' + rtrim(@client_db) + '.dbo.udsp_polling_step2 ' 
							execute @r = @sqlstring @client_table, @host_info, @bran_code, @direction
							if (@r = 1) 
								begin
									begin transaction update_log
										update poll_log with(rowlock) set status = 'P', poll_msg = 'udsp_polling_step2 Completed !' where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme
									commit transaction update_log
								end

							if ((select status from poll_log (nolock) where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme) <> 'P') 
								begin
									rollback transaction poll_data
									update poll_log with(rowlock) set status = 'E', poll_msg = 'ERROR - udsp_polling_step2 ! ' + rtrim(@client_table), last_update_time = getdate() where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme
									goto error_all
								end
							else
								begin	
									begin transaction update_log
										update poll_log with(rowlock) set status = 'I', poll_msg = 'Next Table !', last_update_time = getdate() where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme
									commit transaction update_log
								end

							fetch poll_info_cursor into 
								@bran_code, @poll_grp, @poll_scheme, @poll_count, @status, @direction, @last_update_time, @chk_exists_data, @client_table, @host_table, @host_computer_name, 
								@host_db, @client_computer_name, @client_db
						end
						
						update poll_log with(rowlock) set status = 'C', poll_msg = 'All Tables Polling Completed !', last_update_time = getdate() where bran_code = @temp_bran_code and poll_grp = @temp_poll_grp and poll_scheme = @temp_poll_scheme
						update poll_branch_scheme with(rowlock) set status = 'C', last_update_time = getdate() where bran_code = @temp_bran_code and poll_grp = @temp_poll_grp and poll_scheme = @temp_poll_scheme
				commit transaction
			end
		else
			update poll_log with(rowlock) set status = 'E', poll_msg = 'Incorrect Client Computer Name !', last_update_time = getdate() where bran_code = @bran_code and poll_grp = @poll_grp and poll_scheme = @poll_scheme

error_all:

		while (@temp_bran_code = @bran_code) and (@@FETCH_STATUS <> -1 and @temp_poll_scheme = @poll_scheme)
			begin
				fetch poll_info_cursor into 
					@bran_code, @poll_grp, @poll_scheme, @poll_count, @status, @direction, @last_update_time, @chk_exists_data, @client_table, @host_table, @host_computer_name, 
					@host_db, @client_computer_name, @client_db
			end
	end

	if upper(@poll_type) = 'M'
		update poll_branch_scheme with(rowlock) set poll_count = 0, status = '', sp_status = '', last_update_time = getdate() from poll_branch_scheme where status = 'C'
	else
		update poll_branch_scheme with(rowlock) set poll_count = 0, status = '', sp_status = '', last_update_time = getdate() from poll_branch_scheme where status = 'C' and poll_grp = @poll_grp


close poll_info_cursor
deallocate poll_info_cursor
drop table #xx
drop table #poll_info

return 1


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO