package ru.stonks.algorithm.core.domain

import ru.stonks.algorithm.core.domain.usecase.CalculatePortfolio

trait AlgorithmModule[F[_]] {
  def calculatePortfolio: CalculatePortfolio[F]
}
