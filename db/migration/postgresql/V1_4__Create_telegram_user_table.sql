CREATE TABLE IF NOT EXISTS telegram_user
(
    id             INT                      NOT NULL,
    system_user_id BIGINT                   NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_telegram_user_id PRIMARY KEY (id),
    CONSTRAINT fk_telegram_user_system_user FOREIGN KEY (system_user_id) REFERENCES system_user (id)
);
