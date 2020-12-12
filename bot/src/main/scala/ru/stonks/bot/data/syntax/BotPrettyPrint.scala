package ru.stonks.bot.data.syntax

import ru.stonks.entity.algorithm.Portfolio

import scala.math.BigDecimal.RoundingMode

trait BotPrettyPrint[M] {
  def prettyPrint(m: M): String
}

object BotPrettyPrint {
  def apply[M](implicit instance: BotPrettyPrint[M]): BotPrettyPrint[M] = instance

  implicit def botPrettyPrintSyntax[M: BotPrettyPrint](m: M): BotPrettyPrintOps[M] = new BotPrettyPrintOps[M](m)

  final class BotPrettyPrintOps[M: BotPrettyPrint](m: M) {
    def prettyPrint: String = BotPrettyPrint[M].prettyPrint(m)
  }

  implicit val portfolioBotPrettyPrint: BotPrettyPrint[Portfolio] = (m: Portfolio) => {
    val totalSum  = m.portfolioStocks.map(_.sum).sum.setScale(1, RoundingMode.HALF_UP)
    val portfolio = m.portfolioStocks
      .filter(_.count > 0)
      .sortBy(_.sum)(Ordering[BigDecimal].reverse)
      .map { stock =>
        s"${stock.company.ticker} => ${stock.count} (${stock.sum.setScale(0, RoundingMode.HALF_UP)} $$)"
      }.mkString("\n")

    s"""$portfolio
       |Portfolio total sum: $totalSum $$
       |""".stripMargin
  }
}
