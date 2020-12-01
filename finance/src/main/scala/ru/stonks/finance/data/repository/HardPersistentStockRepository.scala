package ru.stonks.finance.data.repository

import cats.Applicative
import cats.data.NonEmptyList
import cats.effect.Sync
import doobie.implicits._
import doobie.util.fragments
import doobie.util.transactor.Transactor
import doobie.util.update.Update
import ru.stonks.entity.finance.{Company, Stock}
import ru.stonks.finance.domain.repository.StockRepository

class HardPersistentStockRepository[F[_] : Sync](
  transactor: Transactor[F]
) extends StockRepository[F] {

  override def saveAll(companiesToStocks: List[(Company, Stock)]): F[Boolean] = {
    val upsertSql =
      s""" insert into stock(ticker, dollars_price, volume) values (?, ?, ?)
         | on conflict (ticker) do update set
         | dollars_price = excluded.dollars_price,
         | volume = excluded.volume
         |""".stripMargin
    Update[(String, BigDecimal, Long)](upsertSql)
      .updateMany(companiesToStocks.map { case (company, stock) =>
        (company.ticker, stock.dollarsPrice, stock.volume)
      })
      .map(_ => true)
      .transact(transactor)
  }

  override def findAllByCompanies(
    companies: List[Company]
  ): F[List[(Company, Stock)]] = NonEmptyList.fromList(companies) match {
    case None => Applicative[F].pure(List.empty)
    case Some(nonEmptyCompanies) =>
      (fr"select ticker, dollars_price, volume from stock where" ++ fragments.in(fr"ticker", nonEmptyCompanies))
        .query[(Company, Stock)]
        .to[List]
        .transact(transactor)
  }
}
