package ru.stonks.finance.domain.usecase

import cats.Parallel
import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.parallel._
import ru.stonks.entity.finance.MarketIndex
import ru.stonks.finance.core.domain.usecase._

class RefreshAllFinanceRepositoriesImpl[F[_] : Sync : Parallel](
  refreshCompaniesRepository: RefreshCompaniesRepository[F],
  getCompanies: GetCompanies[F],
  refreshMarketCapitalizationRepository: RefreshMarketCapitalizationRepository[F],
  refreshStockRepository: RefreshStockRepository[F]
) extends RefreshAllFinanceRepositories[F] {

  override def run(marketIndexes: List[MarketIndex]): F[List[MarketIndex]] = for {
    updatedMarketIndexes <- refreshCompaniesRepository.run(marketIndexes)
    allCompanies <- updatedMarketIndexes.map(getCompanies.run).parFlatSequence
    uniqueCompanies = allCompanies.distinct
    _ <- (
      refreshMarketCapitalizationRepository.run(uniqueCompanies),
      refreshStockRepository.run(uniqueCompanies)
      ).parTupled
  } yield updatedMarketIndexes
}
