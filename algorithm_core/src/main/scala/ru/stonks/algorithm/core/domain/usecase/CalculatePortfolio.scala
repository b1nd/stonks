package ru.stonks.algorithm.core.domain.usecase

import ru.stonks.entity.algorithm._

trait CalculatePortfolio[F[_]] {
  def run(params: CalculatePortfolioParams): F[Either[CalculatePortfolioError, Portfolio]]
}
