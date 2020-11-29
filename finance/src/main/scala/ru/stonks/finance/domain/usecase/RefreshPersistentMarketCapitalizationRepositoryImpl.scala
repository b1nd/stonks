package ru.stonks.finance.domain.usecase

import cats.Parallel
import cats.effect.{Concurrent, Sync}
import cats.syntax.flatMap._
import cats.syntax.functor._
import ru.stonks.entity.finance.Company
import ru.stonks.finance.core.domain.usecase.RefreshPersistentMarketCapitalizationRepository
import ru.stonks.finance.domain.repository._

class RefreshPersistentMarketCapitalizationRepositoryImpl[F[_] : Sync : Concurrent : Parallel](
  marketCapitalizationRepository: MarketCapitalizationRepository[F],
  persistentMarketCapitalizationRepository: PersistentMarketCapitalizationRepository[F]
) extends RefreshPersistentMarketCapitalizationRepository[F] {

  override def run(companies: List[Company]): F[Boolean] = for {
    // fixme: fails if parallelism > 1
    companiesToCapitalization <- Concurrent.parSequenceN(1)(companies.map { company =>
      marketCapitalizationRepository.get(company).map(_.map { capitalization =>
        (company, capitalization)
      })
    })
    isCompaniesSaved <- persistentMarketCapitalizationRepository
      .saveAll(companiesToCapitalization.flatten)
  } yield isCompaniesSaved
}
