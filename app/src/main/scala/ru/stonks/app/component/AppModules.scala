package ru.stonks.app.component

import cats.effect.{Concurrent, ContextShift, Sync}
import cats.{MonadError, Parallel}
import doobie.util.transactor.Transactor
import org.http4s.client.Client
import ru.stonks.algorithm.data.di.AlgorithmModuleImpl
import ru.stonks.app.config.FinanceApiConfig
import ru.stonks.finance.data.client.FinanceApiClientCredentials
import ru.stonks.finance.data.di.FinanceModuleImpl
import ru.stonks.nasdaq.data.client.NasdaqApiClientCredentials
import ru.stonks.nasdaq.data.di.NasdaqModuleImpl

trait AppModules[F[_]] {
  def nasdaqModule: NasdaqModuleImpl[F]
  def financeModule: FinanceModuleImpl[F]
  def algorithmModule: AlgorithmModuleImpl[F]
}

object AppModules {
  def apply[F[_] : Sync : ContextShift : Parallel : Concurrent](
    financeApiConfig: FinanceApiConfig,
    transactor: Transactor[F],
    client: Client[F])(implicit
    monadError: MonadError[F, Throwable]
  ): AppModules[F] = new AppModules[F] {

    override lazy val nasdaqModule: NasdaqModuleImpl[F] = new NasdaqModuleImpl[F](
      nasdaqApiClientCredentials = NasdaqApiClientCredentials(
        baseUrl = financeApiConfig.baseUrl,
        key = financeApiConfig.apiKey),
      client = client,
      transactor = transactor
    )

    override lazy val financeModule: FinanceModuleImpl[F] = new FinanceModuleImpl[F](
      nasdaqModule,
      financeApiClientCredentials = FinanceApiClientCredentials(
        baseUrl = financeApiConfig.baseUrl,
        key = financeApiConfig.apiKey),
      client = client,
      transactor = transactor
    )

    override lazy val algorithmModule: AlgorithmModuleImpl[F] = new AlgorithmModuleImpl[F](
      financeModule
    )
  }
}
