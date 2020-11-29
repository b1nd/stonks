package ru.stonks.finance.domain.usecase

import cats.effect.Sync
import ru.stonks.entity.finance.{Company, Stock}
import ru.stonks.finance.core.domain.usecase.GetCompaniesStocks
import ru.stonks.finance.domain.repository.PersistentStockRepository

class GetCompaniesStocksImpl[F[_] : Sync](
  persistentStockRepository: PersistentStockRepository[F]
) extends GetCompaniesStocks[F] {

  override def run(companies: List[Company]): F[Map[Company, Stock]] = {
    persistentStockRepository.findAllByCompanies(companies)
  }
}
