package ru.stonks.finance.domain.usecase

import cats.Applicative
import cats.effect.Sync
import ru.stonks.entity.finance.{Company, MarketIndex, NasdaqIndex}
import ru.stonks.finance.core.domain.usecase.GetCompanies
import ru.stonks.nasdaq.core.domain.usecase.GetNasdaqCompanies

class GetCompaniesImpl[F[_] : Sync](
  getNasdaqCompanies: GetNasdaqCompanies[F]
) extends GetCompanies[F] {

  override def run(marketIndex: MarketIndex): F[List[Company]] = marketIndex match {
    case NasdaqIndex => getNasdaqCompanies.run
    case _ => Applicative[F].pure(Nil)
  }
}
