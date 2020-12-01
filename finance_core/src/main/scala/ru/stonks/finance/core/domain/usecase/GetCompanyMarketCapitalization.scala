package ru.stonks.finance.core.domain.usecase

import ru.stonks.entity.finance._

trait GetCompanyMarketCapitalization[F[_]] {
  def run(company: Company): F[Option[MarketCapitalization]]
}
