CREATE SEQUENCE IF NOT EXISTS pub_building_trading_order_sequence START 1 INCREMENT 1;

CREATE TABLE IF NOT EXISTS pub_building_trading_order
(
    id                    BIGINT           NOT NULL PRIMARY KEY,
    account_id            VARCHAR(24)      NOT NULL,
    building_id           BIGINT           NOT NULL REFERENCES pub_building (id),
    configuration_id      VARCHAR(24)      NOT NULL,
    data                  json             NOT NULL,
    created_date          BIGINT           NOT NULL,
    last_modified_date    BIGINT           NOT NULL
);
