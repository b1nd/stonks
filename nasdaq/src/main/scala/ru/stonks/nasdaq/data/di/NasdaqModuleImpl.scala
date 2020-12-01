package ru.stonks.nasdaq.data.di

import cats.effect.{ContextShift, Sync}
import cats.{MonadError, Parallel}
import com.softwaremill.macwire._
import doobie.util.transactor.Transactor
import org.http4s.client.Client
import ru.stonks.nasdaq.core.domain.NasdaqModule
import ru.stonks.nasdaq.core.domain.usecase._
import ru.stonks.nasdaq.data.client.{NasdaqApiClientCredentials, RealTimeNasdaqCompaniesClient}
import ru.stonks.nasdaq.data.repository._
import ru.stonks.nasdaq.domain.client.NasdaqCompaniesClient
import ru.stonks.nasdaq.domain.repository.NasdaqCompaniesRepository
import ru.stonks.nasdaq.domain.usecase._

class NasdaqModuleImpl[F[_]](
  nasdaqApiClientCredentials: NasdaqApiClientCredentials,
  client: Client[F],
  transactor: Transactor[F])(implicit
  sync: Sync[F],
  parallel: Parallel[F],
  contextShift: ContextShift[F],
  monadError: MonadError[F, Throwable]
) extends NasdaqModule[F] {

  lazy val nasdaqCompaniesClient: NasdaqCompaniesClient[F]
  = wire[RealTimeNasdaqCompaniesClient[F]]

  lazy val nasdaqCompaniesRepository: NasdaqCompaniesRepository[F]
  = wire[HardPersistentNasdaqCompaniesRepository[F]]

  lazy val getNasdaqCompanies: GetNasdaqCompanies[F]
  = wire[GetNasdaqCompaniesImpl[F]]

  lazy val refreshNasdaqCompaniesRepository: RefreshNasdaqCompaniesRepository[F]
  = wire[RefreshNasdaqCompaniesRepositoryImpl[F]]
}
