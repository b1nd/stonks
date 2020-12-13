CREATE TABLE IF NOT EXISTS telegram_bot_user_command
(
    id               BIGSERIAL                NOT NULL,
    telegram_user_id INT                      NOT NULL,
    command          TEXT                     NOT NULL,
    input            TEXT,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_telegram_bot_user_command_id PRIMARY KEY (id),
    CONSTRAINT fk_telegram_bot_user_command_telegram_user FOREIGN KEY (telegram_user_id) REFERENCES telegram_user (id)
);
