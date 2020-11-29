package ru.stonks.app.component

import cats.effect.{Async, Blocker, ContextShift, Resource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import ru.stonks.app.config.DatabaseConfig

class AppDatabase[F[_] : Async : ContextShift](databaseConfig: DatabaseConfig) {
  def run: Resource[F, HikariTransactor[F]] = for {
    ce <- ExecutionContexts.fixedThreadPool(databaseConfig.pool)
    be <- Blocker[F]
    tx <-
      HikariTransactor.newHikariTransactor(
        databaseConfig.driver,
        databaseConfig.url,
        databaseConfig.user,
        databaseConfig.password,
        ce,
        be)
  } yield tx
}

object AppDatabase {
  def apply[F[_]](databaseConfig: DatabaseConfig)(implicit
    contextShift: ContextShift[F],
    async: Async[F]
  ): AppDatabase[F] = new AppDatabase(databaseConfig)
}
