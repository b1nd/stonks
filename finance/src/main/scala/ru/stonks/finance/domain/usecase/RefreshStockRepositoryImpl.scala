package ru.stonks.finance.domain.usecase

import cats.effect.{ContextShift, Sync}
import cats.syntax.flatMap._
import cats.syntax.functor._
import ru.stonks.entity.finance.Company
import ru.stonks.finance.core.domain.usecase.RefreshStockRepository
import ru.stonks.finance.domain.client.StockClient
import ru.stonks.finance.domain.repository.StockRepository

class RefreshStockRepositoryImpl[F[_] : Sync : ContextShift](
  stockClient: StockClient[F],
  stockRepository: StockRepository[F]
) extends RefreshStockRepository[F] {

  override def run(companies: List[Company]): F[Boolean] = for {
    companiesToStocks <- stockClient.getAllByCompanies(companies)
    isSuccessfullySaved <- stockRepository.saveAll(companiesToStocks.toList)
  } yield isSuccessfullySaved
}
