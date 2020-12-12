CREATE TABLE IF NOT EXISTS stock
(
    ticker        TEXT                     NOT NULL,
    dollars_price DECIMAL                  NOT NULL,
    volume        BIGINT,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_stock_ticker PRIMARY KEY (ticker)
);
