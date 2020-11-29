package ru.stonks.nasdaq.data.di

import cats.{MonadError, Parallel}
import cats.effect.{ContextShift, Sync}
import com.softwaremill.macwire._
import doobie.util.transactor.Transactor
import org.http4s.client.Client
import ru.stonks.nasdaq.core.domain.NasdaqModule
import ru.stonks.nasdaq.core.domain.usecase._
import ru.stonks.nasdaq.data.repository._
import ru.stonks.nasdaq.domain.repository.PersistentNasdaqCompaniesRepository
import ru.stonks.nasdaq.domain.usecase._

class NasdaqModuleImpl[F[_]](
  financeApiBaseUrl: String,
  financeApiKey: String,
  client: Client[F],
  transactor: Transactor[F])(implicit
  sync: Sync[F],
  parallel: Parallel[F],
  contextShift: ContextShift[F],
  monadError: MonadError[F, Throwable]
) extends NasdaqModule[F] {

  private object RefreshPersistentNasdaqCompaniesRepositoryFactory {
    def create(
      persistentNasdaqCompaniesRepository: PersistentNasdaqCompaniesRepository[F]
    ): RefreshPersistentNasdaqCompaniesRepository[F] = {
      val realTimeNasdaqCompaniesRepository = new RealTimeNasdaqCompaniesRepository[F](
        financeApiBaseUrl, financeApiKey, client
      )
      new RefreshPersistentNasdaqCompaniesRepositoryImpl[F](
        realTimeNasdaqCompaniesRepository,
        persistentNasdaqCompaniesRepository
      )
    }
  }

  lazy val persistentNasdaqCompaniesRepository: PersistentNasdaqCompaniesRepository[F]
  = wire[HardPersistentNasdaqCompaniesRepository[F]]

  lazy val getNasdaqCompanies: GetNasdaqCompanies[F]
  = wire[GetNasdaqCompaniesImpl[F]]

  lazy val refreshPersistentNasdaqCompaniesRepository: RefreshPersistentNasdaqCompaniesRepository[F]
  = wireWith(RefreshPersistentNasdaqCompaniesRepositoryFactory.create _)
}
