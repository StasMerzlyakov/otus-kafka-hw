ALTER USER postgres WITH password 'pgpass';

CREATE DATABASE connect;

CREATE TABLE db_event_record (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_time TIMESTAMP WITH TIME ZONE NOT NULL,
    process_id TEXT NOT NULL,
    result_code TEXT NOT NULL
);


CREATE TABLE IF NOT EXISTS speed_result (
    endpoint TEXT PRIMARY KEY,
    tps DOUBLE PRECISION,
    avg DOUBLE PRECISION
);


