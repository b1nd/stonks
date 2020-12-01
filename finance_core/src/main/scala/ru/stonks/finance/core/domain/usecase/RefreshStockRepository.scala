package ru.stonks.finance.core.domain.usecase

import ru.stonks.entity.finance.Company

trait RefreshStockRepository[F[_]] {
  def run(companies: List[Company]): F[Boolean]
}
