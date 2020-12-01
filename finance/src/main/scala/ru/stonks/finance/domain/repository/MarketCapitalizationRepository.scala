package ru.stonks.finance.domain.repository

import ru.stonks.entity.finance.{Company, MarketCapitalization}

trait MarketCapitalizationRepository[F[_]] {
  def find(company: Company): F[Option[MarketCapitalization]]
  def findAllByCompanies(companies: List[Company]): F[List[(Company, MarketCapitalization)]]
  def save(company: Company, marketCapitalization: MarketCapitalization): F[Boolean]
  def saveAll(companiesToCapitalization: List[(Company, MarketCapitalization)]): F[Boolean]
}
