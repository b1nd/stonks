package ru.stonks.finance.domain.usecase

import cats.effect.Sync
import cats.syntax.functor._
import ru.stonks.entity.finance.{Company, MarketIndex}
import ru.stonks.finance.core.domain.usecase._

class GetAllNonexistentCompaniesImpl[F[_] : Sync](
  getCompanies: GetCompanies[F]
) extends GetAllNonexistentCompanies[F] {

  override def run(companiesToCheck: List[Company], marketIndex: MarketIndex): F[List[Company]] = for {
    indexCompanies <- getCompanies.run(marketIndex)
    nonexistentCompanies = companiesToCheck.diff(indexCompanies)
  } yield nonexistentCompanies
}
