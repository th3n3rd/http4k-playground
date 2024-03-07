create table if not exists games
(
    id uuid not null primary key,
    secret varchar(255) not null,
    won bool not null
)
