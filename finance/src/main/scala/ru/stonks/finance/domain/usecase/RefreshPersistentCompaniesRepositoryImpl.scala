package ru.stonks.finance.domain.usecase

import cats.effect.Sync
import cats.syntax.functor._
import cats.syntax.parallel._
import cats.{Applicative, Parallel}
import ru.stonks.entity.finance.{MarketIndex, Nasdaq}
import ru.stonks.finance.core.domain.usecase.RefreshPersistentCompaniesRepository
import ru.stonks.nasdaq.core.domain.usecase.RefreshPersistentNasdaqCompaniesRepository

class RefreshPersistentCompaniesRepositoryImpl[F[_] : Sync : Parallel](
  refreshPersistentNasdaqCompaniesRepository: RefreshPersistentNasdaqCompaniesRepository[F]
) extends RefreshPersistentCompaniesRepository[F] {

  override def run(marketIndexes: List[MarketIndex]): F[List[MarketIndex]] = marketIndexes.map { idx =>
    idx match {
      case Nasdaq => refreshPersistentNasdaqCompaniesRepository.run
        .map(isUpdated => (idx, isUpdated))
      case _ => Applicative[F].pure((idx, false))
    }
  }.parSequence.map(_.collect { case (index, true) => index })
}
