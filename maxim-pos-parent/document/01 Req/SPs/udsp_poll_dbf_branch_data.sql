CREATE  procedure [dbo].[udsp_poll_dbf_branch_data]
@poll_act  smallint=10
 as

declare @bpoll_grp  smallint

/*
poll_grp
90 Pointsoft(DBF) 
91 Qualicom(SQL)   
92 Infrasys (DBF )  
93 Pointsoft(DBF) and  Qualicom (SQL) 
94 越南SB (DBF)
95  infrasys,PointSoft poll  : Infrasys  hist_  and currency, poll PointSoft pmt,employe ,menu
96 NCR

poll_act 
10 history
11 master
12 sales
*/

 if  @poll_act=11
begin
   exec     udsp_gen_pricing  'MAXCCG','20150901',''  ,'F', 'onhouse','F'
end


select brno as bran_code into #bl  from maximdb.dbo.branch ( nolock) where  1=2


if  @poll_act=10
begin
	select  convert(char(8), dateadd(hour, -4,getdate()), 112) as  post_data into  #poll_date
	insert into #poll_date select  convert(char(8), dateadd(day, -1,getdate()), 112) as  post_data 
	insert into #poll_date select  convert(char(8), dateadd(day, -2,getdate()), 112) as  post_data 
/*
	insert into #poll_date select  convert(char(8), dateadd(day, -3,getdate()), 112) as  post_data 
	insert into #poll_date select  convert(char(8), dateadd(day, -4,getdate()), 112) as  post_data 
	insert into #poll_date select  convert(char(8), dateadd(day, -5,getdate()), 112) as  post_data 
	insert into #poll_date select  convert(char(8), dateadd(day, -6,getdate()), 112) as  post_data 
	insert into #poll_date select  convert(char(8), dateadd(day, -7,getdate()), 112) as  post_data 
	insert into #poll_date select  convert(char(8), dateadd(day, -8,getdate()), 112) as  post_data 
	insert into #poll_date select  convert(char(8), dateadd(day, -9,getdate()), 112) as  post_data 
	insert into #poll_date select  convert(char(8), dateadd(day, -10,getdate()), 112) as  post_data 
	insert into #poll_date select  convert(char(8), dateadd(day, -11,getdate()), 112) as  post_data 
	insert into #poll_date select  convert(char(8), dateadd(day, -12,getdate()), 112) as  post_data 
	insert into #poll_date select  convert(char(8), dateadd(day, -13,getdate()), 112) as  post_data 
	insert into #poll_date select  convert(char(8), dateadd(day, -14,getdate()), 112) as  post_data 
	insert into #poll_date select  convert(char(8), dateadd(day, -15,getdate()), 112) as  post_data 
	insert into #poll_date select  convert(char(8), dateadd(day, -16,getdate()), 112) as  post_data 
*/




end




declare @post_data	varchar(10)
declare @branch_code  varchar(10)

declare @status  char(2) -- add 20140407
declare @status_new  char(2) -- add 20140407


--add 20150114 begin
declare @return_code int 
declare @cmd varchar(500)
select @cmd ='dir c:\temp\*.dbf'
select @cmd ='copy C:\temp\temp_dbf\*.dbf c:\temp'
--exec @return_code = master..xp_cmdshell @cmd, no_output
--add 20150114 end



declare poll_dbf_branch_cursor CURSOR
	for 
--	select bran_code,poll_grp,status from poll_branch_scheme ( nolock) where   poll_scheme='HD'  and poll_grp<>99   and status not in('E','X')       order by 1
	select bran_code,poll_grp,status from poll_branch_scheme ( nolock) where   poll_scheme='HD'  and poll_grp<>99   and status not in('E')       order by 1

                             if  @poll_act=10
		declare poll_dbf_cursor CURSOR for select * from  #poll_date (nolock) order by 1
		
		open poll_dbf_branch_cursor
		fetch poll_dbf_branch_cursor into 
			@branch_code,@bpoll_grp,@status
		while (@@FETCH_STATUS <> -1)
		begin

	                    if @poll_act=10 
		    Begin

-- add 20140407
			select @status_new=''
			if @status='' 
			   select  @status_new='1'

			if @status='1' 
                                                   set @status_new='X'

			update poll_branch_scheme set status=@status_new,last_update_time=getdate()  where bran_code=@branch_code and poll_scheme='HD'
-- add 20140407


			open poll_dbf_cursor
			fetch poll_dbf_cursor into 
				@post_data
			while (@@FETCH_STATUS <> -1)
			begin
		                     select 'poll',@branch_code as branch_code ,@post_data as business_date ,@status as status,getdate()
if @branch_code='1823'
	exec udsp_poll_sql2 '1823','ITHK55P01.ncr_pos.dbo.',@post_data
else
                     exec   udsp_poll_dbf_sales_file @branch_code,@post_data

			     FETCH NEXT FROM poll_dbf_cursor INTO @post_data 
			end
			close poll_dbf_cursor
			select @branch_code,'success',getdate()

			update poll_branch_scheme set status='',last_update_time=getdate()   where bran_code=@branch_code and poll_scheme='HD'    -- add 20140407

                                      end

			
 	                       if @poll_act=11  and  @bpoll_grp in ('90','95')
	                       begin
	                           select 'poll pmt and menu',@branch_code
  	                           exec   udsp_gen_menu_cvs @branch_code,'itaroftp','MXEX'
  	                           exec   udsp_poll_pmt @branch_code,'itaroftp','MXEX'
		           --select @branch_code
--                                           exec   udsp_poll_all_txtfile @branch_code,'','','ALL'
                                        end

		        if @poll_act=11 and @bpoll_grp in ('90','92','95')
		        begin	
		             select @branch_code,@bpoll_grp,'udsp_poll_all_txtfile'

		            if @bpoll_grp='95'
		            begin
			  exec   udsp_poll_all_txtfile @branch_code,'','','ALL','I'	
  			 if @branch_code not in('6503','6303')
			     exec   udsp_poll_all_txtfile @branch_code,'','','ALL','P'		
		            end	
		            else
                                                exec   udsp_poll_all_txtfile @branch_code,'','','ALL'		

		        end	


 	                       if @poll_act =12
		       begin
		           select @branch_code
 		           exec   udsp_poll_dbf_sales_file2 @branch_code
		      end	
		      

		     FETCH NEXT FROM poll_dbf_branch_cursor INTO @branch_code ,@bpoll_grp,@status
		end




        close poll_dbf_branch_cursor

deallocate poll_dbf_branch_cursor

--deallocate poll_dbf_cursor



 if  @poll_act=11
begin

  
--  exec udsp_gen_pricing  'MAXCCG','6234',''  ,'T', 'item','F'

--  exec udsp_gen_pricing  'MAXOTH','6303',''  ,'T', 'item','F'

    exec  udsp_gen_menu_cvs4 'MAXMCC','itaroftp','MAXMCC'    
    exec  udsp_gen_menu_cvs4 'MAXEUR','itaroftp','MAXEUR'    
    exec  udsp_gen_menu_cvs4 'MJCMCC','itaroftp','MJCMCC'    
    exec  udsp_gen_menu_cvs4 'MAXCCG','itaroftp','MAXCCG'    
    exec  udsp_gen_menu_cvs4 'MAXOTH','itaroftp','MAXOTH'    
    exec  udsp_gen_menu_cvs4 'MAXCAN','itaroftp','MAXCAN'    
    exec  udsp_gen_menu_cvs4 'MXGMCC','itaroftp','MXGMCC'    


--    exec udsp_poll_pmt_hdr_txtfile '6502','itaroftp','maxmcc','','MS','1'


    exec udsp_poll_pmt_hdr_txtfile 'MAXMCC','itaroftp','maxmcc','','MS','1'
    exec udsp_poll_pmt_hdr_txtfile 'MAXEUR','itaroftp','maxeur','','MS','1'
    exec udsp_poll_pmt_hdr_txtfile 'MJCMCC','itaroftp','MJCMCC','','MS','1'
    exec udsp_poll_pmt_hdr_txtfile 'MAXCCG','itaroftp','MAXCCG','','MS','1'
    exec udsp_poll_pmt_hdr_txtfile 'MAXOTH','itaroftp','MAXOTH','','MS','1'
    exec udsp_poll_pmt_hdr_txtfile 'MAXCAN','itaroftp','MAXCAN','','MS','1'

    exec udsp_poll_pmt_hdr_txtfile 'MXGMCC','itaroftp','MXGMCC','','MS','1'

    update  hopos_chi.dbo.poll_branch_Scheme set sp_status='',last_update_time=getdate()  where sp_status='M' and poll_scheme='OH'

   update  hopos_eur.dbo.poll_branch_Scheme set sp_status='',last_update_time=getdate()  where sp_status='M' and poll_scheme='OH'


--    exec  udsp_poll_pmt '6803','itaroftp','MXEX'
--    exec  udsp_gen_menu_cvs '6803','itaroftp','MXEX'
end


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

GRANT  EXECUTE  ON [dbo].[udsp_poll_dbf_branch_data]  TO [role_pos_support (Gen Master, Polling)]
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO