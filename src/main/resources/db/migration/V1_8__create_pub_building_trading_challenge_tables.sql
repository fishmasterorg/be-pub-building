CREATE SEQUENCE IF NOT EXISTS pub_building_trading_challenge_sequence START 1 INCREMENT 1;

CREATE TABLE IF NOT EXISTS pub_building_trading_challenge
(
    id                   BIGINT      NOT NULL PRIMARY KEY,
    configuration_id     VARCHAR(24) NOT NULL,
    data                 json        NOT NULL,
    challenge_ended_time BIGINT      NOT NULL,
    created_date         BIGINT      NOT NULL,
    last_modified_date   BIGINT      NOT NULL
);
