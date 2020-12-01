package ru.stonks.nasdaq.domain.client

import ru.stonks.entity.finance.Company

trait NasdaqCompaniesClient[F[_]] {
  def getAll: F[List[Company]]
}
