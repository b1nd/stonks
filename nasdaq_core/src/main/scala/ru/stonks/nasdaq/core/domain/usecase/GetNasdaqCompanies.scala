package ru.stonks.nasdaq.core.domain.usecase

import ru.stonks.entity.finance.Company

trait GetNasdaqCompanies[F[_]] {
  def run: F[List[Company]]
}
