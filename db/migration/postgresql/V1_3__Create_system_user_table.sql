CREATE TABLE IF NOT EXISTS system_user
(
    id               BIGSERIAL                NOT NULL,
    is_telegram_user BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_system_user_id PRIMARY KEY (id)
);
