package ru.stonks.finance.core.domain.usecase

import ru.stonks.entity.finance._

trait GetCompaniesStocks[F[_]] {
  def run(companies: List[Company]): F[Map[Company, Stock]]
}
