package ru.stonks.finance.domain.repository

import ru.stonks.entity.finance.{Company, Stock}

trait PersistentStockRepository[F[_]] extends StockRepository[F] {
  def saveAll(companiesToStocks: List[(Company, Stock)]): F[Boolean]
}
