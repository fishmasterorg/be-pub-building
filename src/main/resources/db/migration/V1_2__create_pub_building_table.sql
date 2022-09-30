CREATE SEQUENCE IF NOT EXISTS pub_building_sequence START 1 INCREMENT 1;

CREATE TABLE IF NOT EXISTS pub_building
(
    id                 BIGINT NOT NULL PRIMARY KEY,
    city_id            BIGINT NOT NULL UNIQUE,
    level              INT    NOT NULL DEFAULT 1,
    created_date       BIGINT NOT NULL,
    last_modified_date BIGINT NOT NULL
);
