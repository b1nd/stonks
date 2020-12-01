package ru.stonks.finance.domain.client

import ru.stonks.entity.finance.{Company, MarketCapitalization}

trait MarketCapitalizationClient[F[_]] {
  def get(company: Company): F[Option[MarketCapitalization]]
  def getAll(companies: List[Company]): F[List[(Company, MarketCapitalization)]]
}
