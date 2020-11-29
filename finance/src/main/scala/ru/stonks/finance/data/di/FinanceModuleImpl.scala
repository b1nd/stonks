package ru.stonks.finance.data.di

import cats.effect.{Concurrent, ContextShift, Sync}
import cats.{MonadError, Parallel}
import com.softwaremill.macwire._
import doobie.util.transactor.Transactor
import org.http4s.client.Client
import ru.stonks.finance.core.domain.FinanceModule
import ru.stonks.finance.core.domain.usecase._
import ru.stonks.finance.data.repository._
import ru.stonks.finance.domain.repository._
import ru.stonks.finance.domain.usecase._
import ru.stonks.nasdaq.core.domain.NasdaqModule

class FinanceModuleImpl[F[_]](
  nasdaqModule: NasdaqModule[F],
  financeApiBaseUrl: String,
  financeApiKey: String,
  client: Client[F],
  transactor: Transactor[F])(implicit
  sync: Sync[F],
  contextShift: ContextShift[F],
  parallel: Parallel[F],
  concurrent: Concurrent[F],
  monadError: MonadError[F, Throwable]
) extends FinanceModule[F] {

  import nasdaqModule._

  private object RefreshPersistentStockRepositoryFactory {
    def create(persistentStockRepository: PersistentStockRepository[F]): RefreshPersistentStockRepository[F] = {
      val realTimeStockRepository = new RealTimeStockRepository[F](
        financeApiBaseUrl, financeApiKey, client
      )
      new RefreshPersistentStockRepositoryImpl[F](realTimeStockRepository, persistentStockRepository)
    }
  }

  private object RefreshPersistentMarketCapitalizationRepositoryFactory {
    def create(
      persistentMarketCapitalizationRepository: PersistentMarketCapitalizationRepository[F]
    ): RefreshPersistentMarketCapitalizationRepository[F] = {
      val realTimeMarketCapitalizationRepository = new RealTimeMarketCapitalizationRepository[F](
        financeApiBaseUrl, financeApiKey, client
      )
      new RefreshPersistentMarketCapitalizationRepositoryImpl[F](
        realTimeMarketCapitalizationRepository,
        persistentMarketCapitalizationRepository
      )
    }
  }

  lazy val persistentMarketCapitalizationRepository: PersistentMarketCapitalizationRepository[F]
  = wire[HardPersistentMarketCapitalizationRepository[F]]

  lazy val persistentStockRepository: PersistentStockRepository[F]
  = wire[HardPersistentStockRepository[F]]

  lazy val getCompanies: GetCompanies[F]
  = wire[GetCompaniesImpl[F]]

  lazy val getCompaniesStocks: GetCompaniesStocks[F]
  = wire[GetCompaniesStocksImpl[F]]

  lazy val refreshPersistentCompaniesRepository: RefreshPersistentCompaniesRepository[F]
  = wire[RefreshPersistentCompaniesRepositoryImpl[F]]

  lazy val getCompanyMarketCapitalization: GetCompanyMarketCapitalization[F]
  = wire[GetCompanyMarketCapitalizationImpl[F]]

  lazy val refreshPersistentMarketCapitalizationRepository: RefreshPersistentMarketCapitalizationRepository[F]
  = wireWith(RefreshPersistentMarketCapitalizationRepositoryFactory.create _)

  lazy val refreshPersistentStockRepository: RefreshPersistentStockRepository[F]
  = wireWith(RefreshPersistentStockRepositoryFactory.create _)

}
