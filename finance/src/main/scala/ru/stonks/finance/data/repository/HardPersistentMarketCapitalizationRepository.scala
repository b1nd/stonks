package ru.stonks.finance.data.repository

import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor.Transactor
import doobie.util.update.Update
import ru.stonks.entity.finance.{Company, MarketCapitalization}
import ru.stonks.finance.domain.repository.PersistentMarketCapitalizationRepository

class HardPersistentMarketCapitalizationRepository[F[_] : Sync](
  transactor: Transactor[F]
) extends PersistentMarketCapitalizationRepository[F] {

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

  override def get(company: Company): F[Option[MarketCapitalization]] =
    sql""" select dollars from market_capitalization
         | where ticker = ${company.ticker}
         |""".stripMargin
      .query[MarketCapitalization]
      .option
      .transact(transactor)

  override def saveAll(companiesToCapitalization: List[(Company, MarketCapitalization)]): F[Boolean] = {
    val upsertSql =
      s""" insert into market_capitalization(ticker, dollars) values (?, ?)
         | on conflict (ticker) do update set
         | dollars = ?
         |""".stripMargin
    Update[(String, BigDecimal, BigDecimal)](upsertSql)
      .updateMany(companiesToCapitalization.map { case (company, cap) =>
        (company.ticker, cap.dollars, cap.dollars)
      })
      .map(_ => true)
      .transact(transactor)
  }
}
