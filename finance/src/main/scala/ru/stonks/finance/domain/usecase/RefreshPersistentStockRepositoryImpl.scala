package ru.stonks.finance.domain.usecase

import cats.effect.{ContextShift, Sync}
import cats.syntax.flatMap._
import cats.syntax.functor._
import ru.stonks.entity.finance.Company
import ru.stonks.finance.core.domain.usecase.RefreshPersistentStockRepository
import ru.stonks.finance.domain.repository.{PersistentStockRepository, StockRepository}

class RefreshPersistentStockRepositoryImpl[F[_] : Sync : ContextShift](
  stockRepository: StockRepository[F],
  persistentStockRepository: PersistentStockRepository[F]
) extends RefreshPersistentStockRepository[F] {

  override def run(companies: List[Company]): F[Boolean] = for {
    companiesToStocks <- stockRepository.findAllByCompanies(companies)
    isSuccessfullySaved <- persistentStockRepository.saveAll(companiesToStocks.toList)
  } yield isSuccessfullySaved
}
