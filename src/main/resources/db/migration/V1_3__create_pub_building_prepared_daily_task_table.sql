CREATE SEQUENCE IF NOT EXISTS pub_building_prepared_daily_task_sequence START 1 INCREMENT 1;

CREATE TABLE IF NOT EXISTS pub_building_prepared_daily_task
(
    id                 BIGINT      NOT NULL PRIMARY KEY,
    building_id        BIGINT      NOT NULL REFERENCES pub_building (id),
    week_day           VARCHAR(24) NOT NULL,
    configuration_id   VARCHAR(24) NOT NULL,
    created_date       BIGINT      NOT NULL,
    last_modified_date BIGINT      NOT NULL
);
