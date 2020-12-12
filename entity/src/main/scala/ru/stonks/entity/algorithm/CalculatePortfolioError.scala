package ru.stonks.entity.algorithm

import ru.stonks.entity.finance.{Company, MarketIndex}

sealed trait CalculatePortfolioError {
  def message: String
}

case class EmptyIndexCompaniesError(marketIndex: MarketIndex) extends CalculatePortfolioError {
  override def message: String = s"There are no companies for $marketIndex!"
}

case object AllCompaniesExcludedError extends CalculatePortfolioError {
  override def message: String = "All companies are excluded!"
}

case class NotAllCompaniesHaveCapitalizationError(companies: List[Company]) extends CalculatePortfolioError {
  override def message: String =
    s"Companies ${companies.map(_.ticker).mkString(", ")} have no market capitalization!"
}

case class NotAllCompaniesHaveStocksError(companies: List[Company]) extends CalculatePortfolioError {
  override def message: String =
    s"Companies ${companies.map(_.ticker).mkString(", ")} have no stocks!"
}

case class NotEnoughMoneyError(minStockPrice: BigDecimal) extends CalculatePortfolioError {
  override def message: String =
    s"Not enough money to buy at least one stock, min stock price: $minStockPrice!"
}
