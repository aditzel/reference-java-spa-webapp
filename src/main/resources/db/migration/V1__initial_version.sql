create table stormpath_user_mapping (
  entity_id char(36) not null,
  user_name varchar(64) not null,
  stormpath_url varchar(255) not null,
  primary key (entity_id)
);

create index idx_spum_username
on stormpath_user_mapping (user_name);

create index idx_spum_url
on stormpath_user_mapping (stormpath_url);