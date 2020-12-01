CREATE TABLE IF NOT EXISTS market_capitalization
(
    ticker     TEXT                     NOT NULL,
    dollars    DECIMAL                  NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_market_capitalization_ticker PRIMARY KEY (ticker)
);