package ru.stonks.finance.domain.usecase

import cats.effect.Sync
import cats.syntax.functor._
import ru.stonks.entity.finance.{Company, MarketCapitalization}
import ru.stonks.finance.core.domain.usecase._
import ru.stonks.finance.domain.repository.MarketCapitalizationRepository

class GetCompaniesMarketCapitalizationImpl[F[_] : Sync](
  marketCapitalizationRepository: MarketCapitalizationRepository[F]
) extends GetCompaniesMarketCapitalization[F] {

  override def run(companies: List[Company]): F[Map[Company, MarketCapitalization]] = for {
    companiesToMarketCapitalization <- marketCapitalizationRepository.findAllByCompanies(companies)
  } yield companiesToMarketCapitalization.toMap
}
