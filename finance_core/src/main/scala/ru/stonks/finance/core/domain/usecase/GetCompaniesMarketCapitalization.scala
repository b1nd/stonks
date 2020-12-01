package ru.stonks.finance.core.domain.usecase

import ru.stonks.entity.finance.{Company, MarketCapitalization}

trait GetCompaniesMarketCapitalization[F[_]] {
  def run(companies: List[Company]): F[Map[Company, MarketCapitalization]]
}
