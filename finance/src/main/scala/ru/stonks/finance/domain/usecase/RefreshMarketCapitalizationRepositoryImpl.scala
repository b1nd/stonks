package ru.stonks.finance.domain.usecase

import cats.Parallel
import cats.effect.{Concurrent, Sync}
import cats.syntax.flatMap._
import cats.syntax.functor._
import ru.stonks.entity.finance.Company
import ru.stonks.finance.core.domain.usecase.RefreshMarketCapitalizationRepository
import ru.stonks.finance.domain.client.MarketCapitalizationClient
import ru.stonks.finance.domain.repository._

class RefreshMarketCapitalizationRepositoryImpl[F[_] : Sync : Concurrent : Parallel](
  marketCapitalizationClient: MarketCapitalizationClient[F],
  marketCapitalizationRepository: MarketCapitalizationRepository[F]
) extends RefreshMarketCapitalizationRepository[F] {

  override def run(companies: List[Company]): F[Boolean] = for {
    companiesToCapitalization <- marketCapitalizationClient.getAll(companies)
    isCompaniesSaved <- marketCapitalizationRepository
      .saveAll(companiesToCapitalization)
  } yield isCompaniesSaved
}
