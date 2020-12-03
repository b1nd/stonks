package ru.stonks.entity.algorithm

import ru.stonks.entity.finance.{Company, MarketIndex}

case class CalculatePortfolioParams(
  marketIndex: MarketIndex,
  dollarsSum: BigDecimal,
  indexCapDepth: Option[Int],
  excludeCompanies: List[Company]
)
