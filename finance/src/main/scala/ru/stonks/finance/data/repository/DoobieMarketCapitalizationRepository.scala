package ru.stonks.finance.data.repository

import cats.Applicative
import cats.data.NonEmptyList
import cats.effect.Sync
import doobie.implicits._
import doobie.util.fragments
import doobie.util.transactor.Transactor
import doobie.util.update.Update
import ru.stonks.entity.finance.{Company, MarketCapitalization}
import ru.stonks.finance.domain.repository.MarketCapitalizationRepository

class DoobieMarketCapitalizationRepository[F[_] : Sync](
  transactor: Transactor[F]
) extends MarketCapitalizationRepository[F] {

  override def find(company: Company): F[Option[MarketCapitalization]] =
    sql""" select dollars from market_capitalization
         | where ticker = ${company.ticker}
         |""".stripMargin
      .query[MarketCapitalization]
      .option
      .transact(transactor)

  override def findAllByCompanies(
    companies: List[Company]
  ): F[List[(Company, MarketCapitalization)]] = NonEmptyList.fromList(companies) match {
    case None => Applicative[F].pure(List.empty)
    case Some(nonEmptyCompanies) =>
      (fr"select ticker, dollars from market_capitalization where" ++ fragments.in(fr"ticker", nonEmptyCompanies))
        .query[(Company, MarketCapitalization)]
        .to[List]
        .transact(transactor)
  }

  override def save(
    company: Company,
    marketCapitalization: MarketCapitalization
  ): F[Boolean] =
    sql""" insert into market_capitalization(ticker, dollars)
         | values (${company.ticker}, ${marketCapitalization.dollars})
         | on conflict (ticker) do update set
         | dollars = ${marketCapitalization.dollars}
         |""".stripMargin
      .update
      .run
      .map(_ => true)
      .transact(transactor)

  override def saveAll(companiesToCapitalization: List[(Company, MarketCapitalization)]): F[Boolean] = {
    val upsertSql =
      s""" insert into market_capitalization(ticker, dollars) values (?, ?)
         | on conflict (ticker) do update set
         | dollars = excluded.dollars
         |""".stripMargin
    Update[(String, BigDecimal)](upsertSql)
      .updateMany(companiesToCapitalization.map { case (company, cap) =>
        (company.ticker, cap.dollars)
      })
      .map(_ => true)
      .transact(transactor)
  }
}
