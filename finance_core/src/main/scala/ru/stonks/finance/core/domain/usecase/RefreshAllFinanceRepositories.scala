package ru.stonks.finance.core.domain.usecase

import ru.stonks.entity.finance.MarketIndex

trait RefreshAllFinanceRepositories[F[_]] {
  def run(marketIndexes: List[MarketIndex]): F[List[MarketIndex]]
}
