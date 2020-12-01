package ru.stonks.finance.domain.repository

import ru.stonks.entity.finance.{Company, Stock}

trait StockRepository[F[_]] {
  def saveAll(companiesToStocks: List[(Company, Stock)]): F[Boolean]
  def findAllByCompanies(companies: List[Company]): F[Map[Company, Stock]]
}
