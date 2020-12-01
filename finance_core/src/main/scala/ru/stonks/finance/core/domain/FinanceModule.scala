package ru.stonks.finance.core.domain

import ru.stonks.finance.core.domain.usecase._

trait FinanceModule[F[_]] {
  def getCompanies: GetCompanies[F]
  def getCompaniesStocks: GetCompaniesStocks[F]
  def getCompanyMarketCapitalization: GetCompanyMarketCapitalization[F]
  def getCompaniesMarketCapitalization: GetCompaniesMarketCapitalization[F]
  def refreshCompaniesRepository: RefreshCompaniesRepository[F]
  def refreshMarketCapitalizationRepository: RefreshMarketCapitalizationRepository[F]
  def refreshStockRepository: RefreshStockRepository[F]
  def refreshAllFinanceRepositories: RefreshAllFinanceRepositories[F]
}
