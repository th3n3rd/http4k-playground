create table if not exists players
(
    id uuid not null primary key,
    username varchar(255) not null unique,
    password varchar(255) not null
)
