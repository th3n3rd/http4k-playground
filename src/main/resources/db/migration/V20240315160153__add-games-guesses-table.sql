create table if not exists game_guesses
(
    game_id uuid not null,
    secret varchar(255) not null
)