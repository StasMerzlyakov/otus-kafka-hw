CREATE TABLE db_events(
    event_time TIMESTAMP WITH TIME ZONE NOT NULL,
    process_id TEXT NOT NULL,
    result_code TEXT NOT NULL,
    __deleted TEXT DEFAULT NULL
);


SELECT create_hypertable(
  'db_events', 'event_time',
  partitioning_column => 'process_id',
  number_partitions => 4,
  chunk_time_interval => INTERVAL '1 day');

CREATE TABLE app_events(
    event_time TIMESTAMP WITH TIME ZONE NOT NULL,
    process_id TEXT NOT NULL,
    endpoint   TEXT NOT NULL,
    __deleted TEXT DEFAULT NULL
);


SELECT create_hypertable(
  'app_events', 'event_time',
  partitioning_column => 'process_id',
  number_partitions => 4,
  chunk_time_interval => INTERVAL '1 day');

