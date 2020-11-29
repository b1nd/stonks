package ru.stonks.finance.core.domain

import ru.stonks.finance.core.domain.usecase._

trait FinanceModule[F[_]] {
  def getCompanies: GetCompanies[F]
  def getCompaniesStocks: GetCompaniesStocks[F]
  def refreshPersistentCompaniesRepository: RefreshPersistentCompaniesRepository[F]
  def getCompanyMarketCapitalization: GetCompanyMarketCapitalization[F]
  def refreshPersistentMarketCapitalizationRepository: RefreshPersistentMarketCapitalizationRepository[F]
  def refreshPersistentStockRepository: RefreshPersistentStockRepository[F]
}
