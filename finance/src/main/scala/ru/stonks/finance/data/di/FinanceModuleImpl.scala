package ru.stonks.finance.data.di

import cats.effect.{Concurrent, ContextShift, Sync}
import cats.{MonadError, Parallel}
import com.softwaremill.macwire._
import doobie.util.transactor.Transactor
import org.http4s.client.Client
import ru.stonks.finance.core.domain.FinanceModule
import ru.stonks.finance.core.domain.usecase._
import ru.stonks.finance.data.client._
import ru.stonks.finance.data.repository._
import ru.stonks.finance.domain.client._
import ru.stonks.finance.domain.repository._
import ru.stonks.finance.domain.usecase._
import ru.stonks.nasdaq.core.domain.NasdaqModule

class FinanceModuleImpl[F[_]](
  nasdaqModule: NasdaqModule[F],
  financeApiClientCredentials: FinanceApiClientCredentials,
  client: Client[F],
  transactor: Transactor[F])(implicit
  sync: Sync[F],
  contextShift: ContextShift[F],
  parallel: Parallel[F],
  concurrent: Concurrent[F],
  monadError: MonadError[F, Throwable]
) extends FinanceModule[F] {

  import nasdaqModule._

  lazy val marketCapitalizationClient: MarketCapitalizationClient[F]
  = wire[RealTimeMarketCapitalizationClient[F]]

  lazy val stockClient: StockClient[F]
  = wire[RealTimeStockClient[F]]

  lazy val marketCapitalizationRepository: MarketCapitalizationRepository[F]
  = wire[HardPersistentMarketCapitalizationRepository[F]]

  lazy val stockRepository: StockRepository[F]
  = wire[HardPersistentStockRepository[F]]

  lazy val getCompanies: GetCompanies[F]
  = wire[GetCompaniesImpl[F]]

  lazy val getCompaniesStocks: GetCompaniesStocks[F]
  = wire[GetCompaniesStocksImpl[F]]

  lazy val getCompanyMarketCapitalization: GetCompanyMarketCapitalization[F]
  = wire[GetCompanyMarketCapitalizationImpl[F]]

  lazy val refreshCompaniesRepository: RefreshCompaniesRepository[F]
  = wire[RefreshCompaniesRepositoryImpl[F]]

  lazy val refreshMarketCapitalizationRepository: RefreshMarketCapitalizationRepository[F]
  = wire[RefreshMarketCapitalizationRepositoryImpl[F]]

  lazy val refreshStockRepository: RefreshStockRepository[F]
  = wire[RefreshStockRepositoryImpl[F]]

  lazy val refreshAllFinanceRepositories: RefreshAllFinanceRepositories[F]
  = wire[RefreshAllFinanceRepositoriesImpl[F]]

}
