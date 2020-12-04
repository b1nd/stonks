package ru.stonks.algorithm.domain.usecase

import ru.stonks.algorithm.domain.entity.CalculatePortfolioError
import ru.stonks.entity.algorithm._

trait CalculatePortfolio[F[_]] {
  def run(params: CalculatePortfolioParams): F[Either[CalculatePortfolioError, Portfolio]]
}
