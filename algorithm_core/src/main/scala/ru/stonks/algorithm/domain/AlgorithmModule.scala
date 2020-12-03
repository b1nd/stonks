package ru.stonks.algorithm.domain

import ru.stonks.algorithm.domain.usecase.CalculatePortfolio

trait AlgorithmModule[F[_]] {
  def calculatePortfolio: CalculatePortfolio[F]
}
