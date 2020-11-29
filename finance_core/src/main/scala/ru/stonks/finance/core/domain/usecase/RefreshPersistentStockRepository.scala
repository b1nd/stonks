package ru.stonks.finance.core.domain.usecase

import ru.stonks.entity.finance.Company

trait RefreshPersistentStockRepository[F[_]] {
  def run(companies: List[Company]): F[Boolean]
}
