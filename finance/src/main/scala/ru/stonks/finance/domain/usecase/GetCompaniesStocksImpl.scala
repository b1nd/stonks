package ru.stonks.finance.domain.usecase

import cats.effect.Sync
import cats.syntax.functor._
import ru.stonks.entity.finance.{Company, Stock}
import ru.stonks.finance.core.domain.usecase.GetCompaniesStocks
import ru.stonks.finance.domain.repository.StockRepository

class GetCompaniesStocksImpl[F[_] : Sync](
  stockRepository: StockRepository[F]
) extends GetCompaniesStocks[F] {

  override def run(companies: List[Company]): F[Map[Company, Stock]] = for {
    companiesToStocks <- stockRepository.findAllByCompanies(companies)
  } yield companiesToStocks.toMap
}
