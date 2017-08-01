if exists (select 1
            from  sysobjects
           where  id = object_id('POLL_BRANCH_INFO')
            and   type = 'U')
   drop table POLL_BRANCH_INFO
;

if exists (select 1
            from  sysobjects
           where  id = object_id('BRANCH_MASTER')
            and   type = 'U')
   drop table BRANCH_MASTER
;
   
if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('POLL_BRANCH_SCHEME') and o.name = 'FK_POLL_BRA_REFERENCE_POLL_BRA')
alter table POLL_BRANCH_SCHEME
   drop constraint FK_POLL_BRA_REFERENCE_POLL_BRA
;

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('POLL_BRANCH_SCHEME') and o.name = 'FK_POLL_BRA_REFERENCE_BRANCH_M')
alter table POLL_BRANCH_SCHEME
   drop constraint FK_POLL_BRA_REFERENCE_BRANCH_M
;

if exists (select 1
            from  sysobjects
           where  id = object_id('POLL_BRANCH_SCHEME')
            and   type = 'U')
   drop table POLL_BRANCH_SCHEME
;

if exists (select 1
            from  sysobjects
           where  id = object_id('POLL_SCHEME_INFO')
            and   type = 'U')
   drop table POLL_SCHEME_INFO
;
   
if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('POLL_SCHEME_TABLE_COLUMN') and o.name = 'FK_POLL_SCH_REFERENCE_POLL_SCH')
alter table POLL_SCHEME_TABLE_COLUMN
   drop constraint FK_POLL_SCH_REFERENCE_POLL_SCH
;

if exists (select 1
            from  sysobjects
           where  id = object_id('POLL_SCHEME_TABLE_COLUMN')
            and   type = 'U')
   drop table POLL_SCHEME_TABLE_COLUMN
;
 
if exists (select 1
            from  sysobjects
           where  id = object_id('SCHEDULER_JOB')
            and   type = 'U')
   drop table SCHEDULER_JOB
;

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('SCHEDULER_JOB_LOG') and o.name = 'FK_SCHEDULE_REFERENCE_SCHEDULE')
alter table SCHEDULER_JOB_LOG
   drop constraint FK_SCHEDULE_REFERENCE_SCHEDULE
;

if exists (select 1
            from  sysobjects
           where  id = object_id('SCHEDULER_JOB_LOG')
            and   type = 'U')
   drop table SCHEDULER_JOB_LOG
;

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('TASK_JOB_LOG') and o.name = 'FK_TASK_JOB_REFERENCE_SCHEDULE')
alter table TASK_JOB_LOG
   drop constraint FK_TASK_JOB_REFERENCE_SCHEDULE
;

if exists (select 1
            from  sysobjects
           where  id = object_id('TASK_JOB_LOG')
            and   type = 'U')
   drop table TASK_JOB_LOG
;

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('TASK_JOB_EXCEPTION_DETAIL') and o.name = 'FK_TASK_JOB_REFERENCE_TASK_JOB')
alter table TASK_JOB_EXCEPTION_DETAIL
   drop constraint FK_TASK_JOB_REFERENCE_TASK_JOB
;

if exists (select 1
            from  sysobjects
           where  id = object_id('TASK_JOB_EXCEPTION_DETAIL')
            and   type = 'U')
   drop table TASK_JOB_EXCEPTION_DETAIL
;

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('TASK_JOB_LOG_DETAIL') and o.name = 'FK_TASK_JOB_REFERENCE_TASK_JOB')
alter table TASK_JOB_LOG_DETAIL
   drop constraint FK_TASK_JOB_REFERENCE_TASK_JOB
;

if exists (select 1
            from  sysobjects
           where  id = object_id('TASK_JOB_LOG_DETAIL')
            and   type = 'U')
   drop table TASK_JOB_LOG_DETAIL
;

/*==============================================================*/
/* Table: POLL_BRANCH_INFO                                      */
/*==============================================================*/
create table POLL_BRANCH_INFO (
   POLL_BRANCH_INFO_ID  bigint               not null,
   CLIENT_TYPE          varchar(10)          not null,
   CLIENT_              varchar(200)         not null,
   CLIENT_PORT          int                  null,
   CLIENT_DB            varchar(50)          null,
   "USER"               varchar(50)          null,
   PASSWORD             varchar(200)         null,
   ENABLE               smallint             not null,
   CREATE_USER          varchar(50)          not null,
   CREATE_TIME          datetime             not null,
   LSAT_UPDATE_USER     varchar(50)          not null,
   LAST_UPDATE_TIME     datetime             not null,
   constraint PK_POLL_BRANCH_INFO primary key (POLL_BRANCH_INFO_ID)
)
;

/*==============================================================*/
/* Table: BRANCH_MASTER                                         */
/*==============================================================*/
create table BRANCH_MASTER (
   BRANCH_CODE          varchar(10)          not null,
   BRANCH_CNAME         nvarchar(50)         not null,
   BRANCH_ENAME         nvarchar(50)         not null,
   BRANCH_TYPE          varchar(4)           not null,
   constraint PK_BRANCH_MASTER primary key (BRANCH_CODE)
)
;

/*==============================================================*/
/* Table: POLL_BRANCH_SCHEME                                    */
/*==============================================================*/
create table POLL_BRANCH_SCHEME (
   POLL_BRANCH_SCHEME_ID bigint               not null,
   POLL_BRANCH_ID       bigint               not null,
   BRANCH_CODE          varchar(10)          not null,
   POLL_SCHEME_TYPE     varchar(20)          not null,
   DIRECTION            varchar(40)          not null,
   POLL_SCHEME_NAME     varchar(100)         not null,
   POLL_SCHEME_DESC     varchar(200)         null,
   START_TIME           time                 not null,
   END_TIME             time                 not null,
   ENABLE               smallint             not null,
   CREATE_USER          varchar(50)          not null,
   CREATE_TIME          datetime             not null,
   LAST_UPDATE_USER     varchar(50)          not null,
   LAST_UPDATE_TIME     datetime             not null,
   constraint PK_POLL_BRANCH_SCHEME primary key (POLL_BRANCH_SCHEME_ID)
)
;

alter table POLL_BRANCH_SCHEME
   add constraint FK_POLL_BRA_REFERENCE_POLL_BRA foreign key (POLL_BRANCH_ID)
      references POLL_BRANCH_INFO (POLL_BRANCH_INFO_ID)
;

alter table POLL_BRANCH_SCHEME
   add constraint FK_POLL_BRA_REFERENCE_BRANCH_M foreign key (BRANCH_CODE)
      references BRANCH_MASTER (BRANCH_CODE)
;

/*==============================================================*/
/* Table: POLL_SCHEME_INFO                                      */
/*==============================================================*/
create table POLL_SCHEME_INFO (
   POLL_SCHEME_INFO_ID  bigint               not null,
   POLL_SCHEME_TYPE     varchar(20)          not null,
   CLIENT_TYPE          varchar(20)          not null,
   DESTINATION          varchar(50)          not null,
   DEST_CHECK_SUM_COLS  varchar(200)         null,
   DEST_KEY_COLUMNS     varchar(200)         null,
   SOURCE               varchar(50)          not null,
   SRC_CHECK_SUM_COLS   varchar(200)         null,
   SRC_KEY_COLUMNS      varchar(200)         null,
   DELIMITER            varchar(10)          null,
   IS_OVERRIDE          smallint             not null,
   CREATE_USER          varchar(50)          not null,
   CREATE_TIME          datetime             not null,
   LAST_UPDATE_USER     varchar(50)          not null,
   LAST_UPDATE_TIME     datetime             not null,
   constraint PK_POLL_SCHEME_INFO primary key (POLL_SCHEME_INFO_ID)
)
;

/*==============================================================*/
/* Table: POLL_SCHEME_TABLE_COLUMN                              */
/*==============================================================*/
create table POLL_SCHEME_TABLE_COLUMN (
   POLL_SCHEME_TABLE_COLUMN_ID bigint               not null,
   POLL_SCHEME_INFO_ID  bigint               not null,
   SEQ                  smallint             not null,
   FROM_COLUMN_NAME     varchar(20)          not null,
   FROM_COLUMN_FORMAT   varchar(20)          null,
   FROM_COLUMN_LENGTH   smallint             null,
   FROM_COLUMN_PRECISION smallint             null,
   TO_COLUMN_NAME       varchar(20)          not null,
   TO_COLUMN_FORMAT     varchar(20)          null,
   TO_COLUMN_LENGTH     smallint             null,
   TO_COLUMN_PRECISION  smallint             null,
   CREATE_USER          varchar(50)          not null,
   CREATE_TIME          datetime             not null,
   LAST_UPDATE_USER     varchar(50)          not null,
   LAST_UPDATE_TIME     datetime             not null,
   constraint PK_POLL_SCHEME_TABLE_COLUMN primary key (POLL_SCHEME_TABLE_COLUMN_ID)
)
;

alter table POLL_SCHEME_TABLE_COLUMN
   add constraint FK_POLL_SCH_REFERENCE_POLL_SCH foreign key (POLL_SCHEME_INFO_ID)
      references POLL_SCHEME_INFO (POLL_SCHEME_INFO_ID)
;
      
/*==============================================================*/
/* Table: SCHEDULER_JOB                                         */
/*==============================================================*/
create table SCHEDULER_JOB (
   SCHEDULER_JOB_ID     bigint               not null,
   JOB_NAME             varchar(50)          not null,
   JOB_DESC             varchar(200)         not null,
   JOB_GROUP            varchar(50)          null,
   ENABLE               smallint             not null,
   POLL_SCHEME_DIRECTION varchar(40)          not null,
   POLL_SCHEME_TYPE     varchar(20)          not null,
   CREATE_USER          varchar(50)          not null,
   CREATE_TIME          datetime             not null,
   LAST_UPDATE_USER     varchar(50)          not null,
   LAST_UPDATE_TIME     datetime             not null,
   constraint PK_SCHEDULER_JOB primary key (SCHEDULER_JOB_ID)
)
;

/*==============================================================*/
/* Table: SCHEDULER_JOB_LOG                                     */
/*==============================================================*/
create table SCHEDULER_JOB_LOG (
   SCHEDULER_JOB_LOG_ID bigint               not null,
   SCHEDULER_JOB_ID     bigint               null,
   START_TIME           datetime             not null,
   END_TIME             datetime             not null,
   NUM_OF_SCHEME_PROCESSED int                  not null,
   LATEST_JOB_IND       char(1)              not null,
   STATUS               varchar(20)          not null,
   CREATE_USER          varchar(50)          not null,
   CREATE_TIME          datetime             not null,
   LAST_UPDATE_USER     varchar(50)          not null,
   LAST_UPDATE_TIME     datetime             not null,
   constraint PK_SCHEDULER_JOB_LOG primary key (SCHEDULER_JOB_LOG_ID)
)
;

alter table SCHEDULER_JOB_LOG
   add constraint FK_SCHEDULE_REFERENCE_SCHEDULE foreign key (SCHEDULER_JOB_ID)
      references SCHEDULER_JOB (SCHEDULER_JOB_ID)
;

/*==============================================================*/
/* Table: TASK_JOB_LOG                                          */
/*==============================================================*/
create table TASK_JOB_LOG (
   TASK_JOB_LOG_ID      bigint               not null,
   SCHEDULER_JOB_LOG_ID bigint               null,
   TASK_NAME            varchar(100)         null,
   POLL_SCHEME_ID       bigint               null,
   POLL_BRANCH_ID       bigint               null,
   DEPEND_ON            bigint               null,
   BRANCH_CODE          varchar(20)          null,
   POLL_SCHEME_NAME     varchar(100)         null,
   POLL_SCHEME_DESC     varchar(200)         null,
   POLL_SCHEME_TYPE     varchar(20)          null,
   DIRECTION            varchar(40)          null,
   START_TIME           datetime             not null,
   END_TIME             datetime             not null,
   LASTEST_JOB_IND      char(1)              not null,
   STATUS               varchar(20)          not null,
   ERROR_CODE           varchar(100)         null,
   ERROR_MSG            varchar(400)         null,
   CREATE_USER          varchar(50)          not null,
   CREATE_TIME          datetime             not null,
   LAST_UPDATE_USER     varchar(50)          not null,
   LAST_UPDATE_TIME     datetime             not null,
   constraint PK_TASK_JOB_LOG primary key (TASK_JOB_LOG_ID)
)
;

alter table TASK_JOB_LOG
   add constraint FK_TASK_JOB_REFERENCE_SCHEDULE foreign key (SCHEDULER_JOB_LOG_ID)
      references SCHEDULER_JOB_LOG (SCHEDULER_JOB_LOG_ID)
;

/*==============================================================*/
/* Table: TASK_JOB_EXCEPTION_DETAIL                             */
/*==============================================================*/
create table TASK_JOB_EXCEPTION_DETAIL (
   TASK_JOB_EXCEPTION_DETAIL_ID bigint               not null,
   TASK_JOB_LOG_ID      bigint               not null,
   SOURCE_DESTINATION   varchar(100)         not null,
   TO_TABLE             varchar(100)         not null,
   EXCEPTION_CONTENT    nvarchar(4000)       null,
   CREATE_TIME          datetime             not null,
   constraint PK_TASK_JOB_EXCEPTION_DETAIL primary key (TASK_JOB_EXCEPTION_DETAIL_ID)
)
;

alter table TASK_JOB_EXCEPTION_DETAIL
   add constraint FK_TASK_JOB_REFERENCE_TASK_JOB foreign key (TASK_JOB_LOG_ID)
      references TASK_JOB_LOG (TASK_JOB_LOG_ID)
;

*==============================================================*/
/* Table: TASK_JOB_LOG_DETAIL                                   */
/*==============================================================*/
create table TASK_JOB_LOG_DETAIL (
   TASK_JOB_LOG_DETAIL_ID bigint               not null,
   TASK_JOB_LOG_ID      bigint               not null,
   SOURCE               varchar(100)         not null,
   DESTINATION          varchar(100)         not null,
   NUM_OF_REC_PROCESSED int                  not null,
   NUM_OF_REC_INSERT    int                  null,
   NUM_OF_REC_UPDATE    int                  null,
   NUM_OF_REC_DELETE    int                  null,
   CREATE_TIME          datetime             not null,
   constraint PK_TASK_JOB_LOG_DETAIL primary key (TASK_JOB_LOG_DETAIL_ID)
)
;

alter table TASK_JOB_LOG_DETAIL
   add constraint FK_TASK_JOB_REFERENCE_TASK_JOB foreign key (TASK_JOB_LOG_ID)
      references TASK_JOB_LOG (TASK_JOB_LOG_ID)
;
