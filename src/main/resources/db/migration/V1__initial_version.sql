create table STORMPATH_USER_MAPPING (
  ID varchar(36) not null,
  USERNAME varchar(64) not null,
  STORMPATH_URL varchar(512) not null,
  primary key (id)
);