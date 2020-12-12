package ru.stonks.app.component

import cats.effect.{Concurrent, ConcurrentEffect, ContextShift, Sync, Timer}
import cats.{MonadError, Parallel}
import doobie.util.transactor.Transactor
import org.http4s.client.Client
import ru.stonks.algorithm.data.di.AlgorithmModuleImpl
import ru.stonks.app.config.FinanceApiConfig
import ru.stonks.bot.data.di.BotModuleImpl
import ru.stonks.finance.data.client.FinanceApiClientCredentials
import ru.stonks.finance.data.di.FinanceModuleImpl
import ru.stonks.nasdaq.data.client.NasdaqApiClientCredentials
import ru.stonks.nasdaq.data.di.NasdaqModuleImpl
import ru.stonks.user.data.di.UserModuleImpl
import telegramium.bots.high.Api

trait AppModules[F[_]] {
  def nasdaqModule: NasdaqModuleImpl[F]
  def financeModule: FinanceModuleImpl[F]
  def algorithmModule: AlgorithmModuleImpl[F]
  def userModule: UserModuleImpl[F]
  def botModule: BotModuleImpl[F]
}

object AppModules {
  def apply[F[_] : Sync : ContextShift : Parallel : Concurrent : ConcurrentEffect : Timer](
    financeApiConfig: FinanceApiConfig,
    transactor: Transactor[F],
    client: Client[F],
    api: Api[F])(implicit
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
      nasdaqModule = nasdaqModule,
      financeApiClientCredentials = FinanceApiClientCredentials(
        baseUrl = financeApiConfig.baseUrl,
        key = financeApiConfig.apiKey),
      client = client,
      transactor = transactor
    )

    override lazy val algorithmModule: AlgorithmModuleImpl[F] = new AlgorithmModuleImpl[F](
      financeModule = financeModule
    )

    override lazy val userModule: UserModuleImpl[F] = new UserModuleImpl[F](
      transactor = transactor
    )

    override def botModule: BotModuleImpl[F] = new BotModuleImpl[F](
      userModule = userModule,
      financeModule = financeModule,
      algorithmModule = algorithmModule,
      api = api,
      transactor = transactor
    )
  }
}
