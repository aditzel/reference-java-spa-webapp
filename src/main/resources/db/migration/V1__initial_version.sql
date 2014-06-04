create table STORMPATH_USER_MAPPING (
  ID char(36) not null default '00000000-0000-0000-0000-000000000000',
  STORMPATH_URL varchar(512) not null,
  primary key (id)
);

create trigger STORMPATH_USER_MAPPING_ONINSERT
before insert on STORMPATH_USER_MAPPING
  for each row
    set New.id = uuid();
