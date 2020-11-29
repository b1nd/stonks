package ru.stonks.finance.data.repository

import cats.Applicative
import cats.data.NonEmptyList
import cats.effect.Sync
import doobie.implicits._
import doobie.util.fragments
import doobie.util.transactor.Transactor
import doobie.util.update.Update
import ru.stonks.entity.finance.{Company, Stock}
import ru.stonks.finance.domain.repository.PersistentStockRepository

class HardPersistentStockRepository[F[_] : Sync](
  transactor: Transactor[F]
) extends PersistentStockRepository[F] {

  override def saveAll(companiesToStocks: List[(Company, Stock)]): F[Boolean] = {
    val upsertSql =
      s""" insert into stock(ticker, dollars_price, volume) values (?, ?, ?)
         | on conflict (ticker) do update set
         | dollars_price = ?,
         | volume = ?
         |""".stripMargin
    Update[(String, BigDecimal, Long, BigDecimal, Long)](upsertSql)
      .updateMany(companiesToStocks.map { case (company, stock) =>
        (company.ticker, stock.dollarsPrice, stock.volume, stock.dollarsPrice, stock.volume)
      })
      .map(_ => true)
      .transact(transactor)
  }

  override def findAllByCompanies(
    companies: List[Company]
  ): F[Map[Company, Stock]] = NonEmptyList.fromList(companies) match {
    case None => Applicative[F].pure(Map.empty)
    case Some(nonEmptyCompanies) =>
      (fr"select ticker, dollars_price, volume from stock where" ++ fragments.in(fr"ticker", nonEmptyCompanies))
        .query[(Company, Stock)]
        .to[List]
        .map(_.toMap)
        .transact(transactor)
  }
}
