package ru.stonks.nasdaq.data.repository

import cats.Applicative
import cats.data.NonEmptyList
import cats.effect.Sync
import doobie.implicits._
import doobie.util.fragments
import doobie.util.transactor.Transactor
import doobie.util.update.Update
import ru.stonks.entity.finance.Company
import ru.stonks.entity.finance.MarketIndex.NasdaqIndex
import ru.stonks.nasdaq.domain.repository.NasdaqCompaniesRepository

class DoobieNasdaqCompaniesRepository[F[_] : Sync](
  transactor: Transactor[F]
) extends NasdaqCompaniesRepository[F] {

  override def saveOrUpdateAll(companies: List[Company]): F[Boolean] = {
    val upsertSql =
      s""" insert into company(ticker, market_index) values (?, lower('$NasdaqIndex'))
         | on conflict (ticker, market_index) do nothing
         |""".stripMargin
    Update[String](upsertSql)
      .updateMany(companies.map(_.ticker))
      .map(_ => true)
      .transact(transactor)
  }

  override def findAll: F[List[Company]] =
    sql""" select (ticker) from company
         | where lower(market_index) = lower(${NasdaqIndex.toString})
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
