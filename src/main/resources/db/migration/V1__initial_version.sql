create table STORMPATH_USER_MAPPING (
  ID char(36) not null,
  USERNAME varchar(64) not null,
  STORMPATH_URL varchar(255) not null,
  primary key (id)
);

create index idx_spum_username
on STORMPATH_USER_MAPPING (USERNAME);

create index idx_spum_url
on STORMPATH_USER_MAPPING (STORMPATH_URL);