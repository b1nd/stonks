package ru.stonks.nasdaq.data.repository

import cats.Applicative
import cats.data.NonEmptyList
import cats.effect.Sync
import doobie.implicits._
import doobie.util.fragments
import doobie.util.transactor.Transactor
import doobie.util.update.Update
import ru.stonks.entity.finance.{Company, Nasdaq}
import ru.stonks.nasdaq.domain.repository.PersistentNasdaqCompaniesRepository

class HardPersistentNasdaqCompaniesRepository[F[_] : Sync](
  transactor: Transactor[F]
) extends PersistentNasdaqCompaniesRepository[F] {

  override def saveOrUpdateAll(companies: List[Company]): F[Boolean] = {
    val upsertSql =
      s""" insert into company(ticker, market_index) values (?, lower('$Nasdaq'))
         | on conflict (ticker, market_index) do nothing
         |""".stripMargin
    Update[Company](upsertSql)
      .updateMany(companies)
      .map(_ => true)
      .transact(transactor)
  }

  override def findAll: F[List[Company]] =
    sql""" select (ticker) from company
         | where lower(market_index) = lower(${Nasdaq.toString})
         |""".stripMargin
      .query[Company]
      .to[List]
      .transact(transactor)

  override def deleteAll(companies: List[Company]): F[Boolean] = NonEmptyList.fromList(companies) match {
    case None => Applicative[F].pure(false)
    case Some(nonEmptyCompanies) =>
      (fr"delete from company where" ++ fragments.in(fr"ticker", nonEmptyCompanies))
        .update
        .run
        .map(_ => true)
        .transact(transactor)
  }
}
