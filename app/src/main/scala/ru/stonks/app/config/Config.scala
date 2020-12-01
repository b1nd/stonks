package ru.stonks.app.config

final case class Config(
  financeApi: FinanceApiConfig,
  db: DatabaseConfig,
  server: ServerConfig,
  client: ClientConfig
)
