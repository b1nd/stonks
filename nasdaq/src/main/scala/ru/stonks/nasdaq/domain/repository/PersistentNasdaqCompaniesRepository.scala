package ru.stonks.nasdaq.domain.repository

import ru.stonks.entity.finance.Company

trait PersistentNasdaqCompaniesRepository[F[_]] extends NasdaqCompaniesRepository[F] {
  def saveOrUpdateAll(companies: List[Company]): F[Boolean]

  def deleteAll(companies: List[Company]): F[Boolean]
}
