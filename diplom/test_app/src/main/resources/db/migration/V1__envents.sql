CREATE TABLE db_event_record (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    step_code VARCHAR(5) NOT NULL, -- Event10500 -> 10500
    project_code VARCHAR(3) NOT NULL,
    process_code VARCHAR(3) NOT NULL,
    event_time TIMESTAMP WITH TIME ZONE NOT NULL,
    event_source VARCHAR(50) NOT NULL,
    process_guid VARCHAR(12) NOT NULL,
    result VARCHAR(3) NOT NULL,
    task_initiator VARCHAR(50) DEFAULT NULL,
    task_recipient VARCHAR(50) DEFAULT NULL,
    event_type VARCHAR(50) DEFAULT NULL,
    message_id VARCHAR(255) DEFAULT NULL,
    message_type VARCHAR(255) DEFAULT NULL,
    details VARCHAR(1024) DEFAULT NULL,
    importance SMALLINT DEFAULT NULL,
)


