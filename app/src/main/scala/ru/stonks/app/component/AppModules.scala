package ru.stonks.app.component

import cats.effect.{Concurrent, ContextShift, Sync}
import cats.{MonadError, Parallel}
import doobie.util.transactor.Transactor
import org.http4s.client.Client
import ru.stonks.app.config.FinanceApiConfig
import ru.stonks.finance.data.di.FinanceModuleImpl
import ru.stonks.nasdaq.data.di.NasdaqModuleImpl

trait AppModules[F[_]] {
  def nasdaqModule: NasdaqModuleImpl[F]

  def financeModule: FinanceModuleImpl[F]
}

object AppModules {
  def apply[F[_] : Sync : ContextShift : Parallel: Concurrent](
    financeApiConfig: FinanceApiConfig,
    transactor: Transactor[F],
    client: Client[F])(implicit
    monadError: MonadError[F, Throwable]
  ): AppModules[F] = new AppModules[F] {

    override lazy val nasdaqModule: NasdaqModuleImpl[F] = new NasdaqModuleImpl[F](
      financeApiBaseUrl = financeApiConfig.baseUrl,
      financeApiKey = financeApiConfig.apiKey,
      client = client,
      xa = transactor
    )

    override lazy val financeModule: FinanceModuleImpl[F] = new FinanceModuleImpl[F](
      nasdaqModule,
      financeApiBaseUrl = financeApiConfig.baseUrl,
      financeApiKey = financeApiConfig.apiKey,
      client = client,
      transactor = transactor
    )

  }
}
