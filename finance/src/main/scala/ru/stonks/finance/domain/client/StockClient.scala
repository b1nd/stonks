package ru.stonks.finance.domain.client

import ru.stonks.entity.finance.{Company, Stock}

trait StockClient[F[_]] {
  def getAllByCompanies(companies: List[Company]): F[Map[Company, Stock]]
}
