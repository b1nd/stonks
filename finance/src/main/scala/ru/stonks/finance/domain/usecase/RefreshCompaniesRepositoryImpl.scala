package ru.stonks.finance.domain.usecase

import cats.effect.Sync
import cats.syntax.functor._
import cats.syntax.parallel._
import cats.{Applicative, Parallel}
import ru.stonks.entity.finance.MarketIndex
import ru.stonks.entity.finance.MarketIndex.NasdaqIndex
import ru.stonks.finance.core.domain.usecase.RefreshCompaniesRepository
import ru.stonks.nasdaq.core.domain.usecase.RefreshNasdaqCompaniesRepository

class RefreshCompaniesRepositoryImpl[F[_] : Sync : Parallel](
  refreshNasdaqCompaniesRepository: RefreshNasdaqCompaniesRepository[F]
) extends RefreshCompaniesRepository[F] {

  override def run(marketIndexes: List[MarketIndex]): F[List[MarketIndex]] = marketIndexes.map { idx =>
    idx match {
      case NasdaqIndex => refreshNasdaqCompaniesRepository.run
        .map(isUpdated => (idx, isUpdated))
      case _ => Applicative[F].pure((idx, false))
    }
  }.parSequence.map(_.collect { case (index, true) => index })
}
