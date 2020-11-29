package ru.stonks.finance.domain.repository

import ru.stonks.entity.finance.{Company, MarketCapitalization}

trait MarketCapitalizationRepository[F[_]] {
  def get(company: Company): F[Option[MarketCapitalization]]
}
