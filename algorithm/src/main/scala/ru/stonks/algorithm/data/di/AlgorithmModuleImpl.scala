package ru.stonks.algorithm.data.di

import cats.Parallel
import cats.effect.Sync
import com.softwaremill.macwire._
import ru.stonks.algorithm.domain.AlgorithmModule
import ru.stonks.algorithm.domain.usecase.{CalculatePortfolio, CalculatePortfolioImpl}
import ru.stonks.finance.core.domain.FinanceModule

class AlgorithmModuleImpl[F[_]: Sync: Parallel](
  financeModule: FinanceModule[F]
) extends AlgorithmModule[F] {
  import financeModule._

  lazy val calculatePortfolio: CalculatePortfolio[F]
  = wire[CalculatePortfolioImpl[F]]
}
