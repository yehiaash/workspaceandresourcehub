/*==============================================================*/
/* DBMS name:      Microsoft SQL Server 2012                    */
/* Created on:     09/05/2026 11:42:28 ?                        */
/*==============================================================*/


if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('LOG') and o.name = 'FK_LOG_LOG_RESERVAT')
alter table LOG
   drop constraint FK_LOG_LOG_RESERVAT
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('LOG') and o.name = 'FK_LOG_LOG2_EQUIPMEN')
alter table LOG
   drop constraint FK_LOG_LOG2_EQUIPMEN
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('RESERVATION') and o.name = 'FK_RESERVAT_BOOKED_IN_WORKSPAC')
alter table RESERVATION
   drop constraint FK_RESERVAT_BOOKED_IN_WORKSPAC
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('RESERVATION') and o.name = 'FK_RESERVAT_MAKES_MEMBER')
alter table RESERVATION
   drop constraint FK_RESERVAT_MAKES_MEMBER
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('WORKSPACE') and o.name = 'FK_WORKSPAC_CONTAINS_URBAN_HU')
alter table WORKSPACE
   drop constraint FK_WORKSPAC_CONTAINS_URBAN_HU
go

if exists (select 1
            from  sysobjects
           where  id = object_id('EQUIPMENT')
            and   type = 'U')
   drop table EQUIPMENT
go

if exists (select 1
            from  sysobjects
           where  id = object_id('LOG')
            and   type = 'U')
   drop table LOG
go

if exists (select 1
            from  sysobjects
           where  id = object_id('MEMBER')
            and   type = 'U')
   drop table MEMBER
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('RESERVATION')
            and   name  = 'MAKES_FK'
            and   indid > 0
            and   indid < 255)
   drop index RESERVATION.MAKES_FK
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('RESERVATION')
            and   name  = 'BOOKED_IN_FK'
            and   indid > 0
            and   indid < 255)
   drop index RESERVATION.BOOKED_IN_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('RESERVATION')
            and   type = 'U')
   drop table RESERVATION
go

if exists (select 1
            from  sysobjects
           where  id = object_id('URBAN_HUB')
            and   type = 'U')
   drop table URBAN_HUB
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('WORKSPACE')
            and   name  = 'CONTAINS_FK'
            and   indid > 0
            and   indid < 255)
   drop index WORKSPACE.CONTAINS_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('WORKSPACE')
            and   type = 'U')
   drop table WORKSPACE
go

/*==============================================================*/
/* Table: EQUIPMENT                                             */
/*==============================================================*/
create table EQUIPMENT (
   EQUIPMENTID          int                  not null,
   EQUIPMENTNAME        varchar(50)          not null,
   EQUIPMENTTYPE        varchar(30)          not null,
   USAGERATE            decimal(10,2)        not null,
   AVAILABILITYSTATUS   varchar(15)          not null,
   constraint PK_EQUIPMENT primary key nonclustered (EQUIPMENTID)
)
go

/*==============================================================*/
/* Table: LOG                                                   */
/*==============================================================*/
create table LOG (
   RESERVATION_ID       int                  not null,
   EQUIPMENTID          int                  not null,
   LOG_ID               int                  not null,
   DURATION             int                  null,
   constraint PK_LOG primary key (RESERVATION_ID, EQUIPMENTID)
)
go

/*==============================================================*/
/* Table: MEMBER                                                */
/*==============================================================*/
create table MEMBER (
   MEMBER_ID            int                  not null,
   FULLNAME             varchar(50)          not null,
   EMAIL                varchar(50)          null,
   PHONE                varchar(20)          not null,
   AFFILIATION          varchar(100)         null,
   REGISTRATIONDATE     datetime             not null,
   constraint PK_MEMBER primary key nonclustered (MEMBER_ID)
)
go

/*==============================================================*/
/* Table: RESERVATION                                           */
/*==============================================================*/
create table RESERVATION (
   RESERVATION_ID       int                  not null,
   WORKSPACE_ID         int                  not null,
   MEMBER_ID            int                  not null,
   STATUS               varchar(20)          not null,
   RESERVATIONDATE      datetime             not null,
   STARTTIME            datetime             not null,
   ENDTIME              datetime             not null,
   constraint PK_RESERVATION primary key nonclustered (RESERVATION_ID)
)
go

/*==============================================================*/
/* Index: BOOKED_IN_FK                                          */
/*==============================================================*/
create index BOOKED_IN_FK on RESERVATION (
WORKSPACE_ID ASC
)
go

/*==============================================================*/
/* Index: MAKES_FK                                              */
/*==============================================================*/
create index MAKES_FK on RESERVATION (
MEMBER_ID ASC
)
go

/*==============================================================*/
/* Table: URBAN_HUB                                             */
/*==============================================================*/
create table URBAN_HUB (
   HUB_ID               int                  not null,
   HUB_NAME             varchar(20)          not null,
   ARCHITECTUAL_LAYOUT  varchar(50)          not null,
   DISTRICT             varchar(50)          not null,
   constraint PK_URBAN_HUB primary key nonclustered (HUB_ID)
)
go

/*==============================================================*/
/* Table: WORKSPACE                                             */
/*==============================================================*/
create table WORKSPACE (
   WORKSPACE_ID         int                  not null,
   HUB_ID               int                  not null,
   TYPE                 varchar(50)          not null,
   RATE                 decimal(10,2)        null,
   AVAILABILITY_STATUS  varchar(20)          not null,
   constraint PK_WORKSPACE primary key nonclustered (WORKSPACE_ID)
)
go

/*==============================================================*/
/* Index: CONTAINS_FK                                           */
/*==============================================================*/
create index CONTAINS_FK on WORKSPACE (
HUB_ID ASC
)
go

alter table LOG
   add constraint FK_LOG_LOG_RESERVAT foreign key (RESERVATION_ID)
      references RESERVATION (RESERVATION_ID)
go

alter table LOG
   add constraint FK_LOG_LOG2_EQUIPMEN foreign key (EQUIPMENTID)
      references EQUIPMENT (EQUIPMENTID)
go

alter table RESERVATION
   add constraint FK_RESERVAT_BOOKED_IN_WORKSPAC foreign key (WORKSPACE_ID)
      references WORKSPACE (WORKSPACE_ID)
go

alter table RESERVATION
   add constraint FK_RESERVAT_MAKES_MEMBER foreign key (MEMBER_ID)
      references MEMBER (MEMBER_ID)
go

alter table WORKSPACE
   add constraint FK_WORKSPAC_CONTAINS_URBAN_HU foreign key (HUB_ID)
      references URBAN_HUB (HUB_ID)
go

