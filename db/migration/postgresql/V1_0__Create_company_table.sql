CREATE TABLE IF NOT EXISTS company
(
    ticker       TEXT                     NOT NULL,
    market_index TEXT                     NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_company_ticker PRIMARY KEY (ticker, market_index)
);
