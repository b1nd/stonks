package ru.stonks.finance.domain.repository

import ru.stonks.entity.finance.{Company, MarketCapitalization}

trait PersistentMarketCapitalizationRepository[F[_]] extends MarketCapitalizationRepository[F] {
  def save(company: Company, marketCapitalization: MarketCapitalization): F[Boolean]

  def saveAll(companiesToCapitalization: List[(Company, MarketCapitalization)]): F[Boolean]
}
