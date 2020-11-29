package ru.stonks.app.config

final case class DatabaseConfig(
  driver: String,
  url: String,
  user: String,
  password: String,
  pool: Int
)
