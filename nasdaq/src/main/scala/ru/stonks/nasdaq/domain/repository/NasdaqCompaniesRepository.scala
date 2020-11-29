package ru.stonks.nasdaq.domain.repository

import ru.stonks.entity.finance.Company

trait NasdaqCompaniesRepository[F[_]] {
  def findAll: F[List[Company]]
}
