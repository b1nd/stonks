package ru.stonks.nasdaq.core.domain

import ru.stonks.nasdaq.core.domain.usecase._

trait NasdaqModule[F[_]] {
  def getNasdaqCompanies: GetNasdaqCompanies[F]
  def refreshPersistentNasdaqCompaniesRepository: RefreshPersistentNasdaqCompaniesRepository[F]
}
