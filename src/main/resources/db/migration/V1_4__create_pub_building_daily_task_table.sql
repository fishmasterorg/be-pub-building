CREATE SEQUENCE IF NOT EXISTS pub_building_daily_task_sequence START 1 INCREMENT 1;

CREATE TABLE IF NOT EXISTS pub_building_daily_task
(
    id                 BIGINT      NOT NULL PRIMARY KEY,
    account_id         VARCHAR(24) NOT NULL,
    building_id        BIGINT      NOT NULL REFERENCES pub_building (id),
    character_ids      VARCHAR     NOT NULL,
    configuration_id   VARCHAR(24) NOT NULL,
    current_progress   INT         NOT NULL,
    final_progress     INT         NOT NULL,
    status             VARCHAR(24) NOT NULL,
    created_date       BIGINT      NOT NULL,
    last_modified_date BIGINT      NOT NULL
);
