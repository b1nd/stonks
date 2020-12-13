package ru.stonks.algorithm.data.di

import cats.Parallel
import cats.effect.Sync
import com.softwaremill.macwire._
import ru.stonks.algorithm.core.domain.AlgorithmModule
import ru.stonks.algorithm.core.domain.usecase._
import ru.stonks.algorithm.domain.usecase.CalculatePortfolioImpl
import ru.stonks.finance.core.domain.FinanceModule

class AlgorithmModuleImpl[F[_]: Sync: Parallel](
  financeModule: FinanceModule[F]
) extends AlgorithmModule[F] {
  import financeModule._

  override lazy val calculatePortfolio: CalculatePortfolio[F]
  = wire[CalculatePortfolioImpl[F]]
}
