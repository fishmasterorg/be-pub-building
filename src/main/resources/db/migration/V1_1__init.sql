CREATE OR REPLACE FUNCTION now_epoch_timestamp() RETURNS bigint AS
$$
BEGIN
    RETURN (extract(epoch from now() at time zone 'utc') * 1000)::bigint;
END;
$$ LANGUAGE plpgsql;

CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START 1 INCREMENT 1;