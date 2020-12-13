package ru.stonks.finance.core.domain.usecase

import ru.stonks.entity.finance.{Company, MarketIndex}

trait GetAllNonexistentCompanies[F[_]] {
  def run(companiesToCheck: List[Company], marketIndex: MarketIndex): F[List[Company]]
}
