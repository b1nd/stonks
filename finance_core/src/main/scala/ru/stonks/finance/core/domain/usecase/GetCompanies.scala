package ru.stonks.finance.core.domain.usecase

import ru.stonks.entity.finance._

trait GetCompanies[F[_]] {
  def run(marketIndex: MarketIndex): F[List[Company]]
}
