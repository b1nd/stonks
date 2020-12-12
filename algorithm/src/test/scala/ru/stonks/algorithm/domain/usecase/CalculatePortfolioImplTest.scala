package ru.stonks.algorithm.domain.usecase

import cats.effect.IO
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import ru.stonks.entity.algorithm._
import ru.stonks.entity.finance.MarketIndex.NasdaqIndex
import ru.stonks.entity.finance._
import ru.stonks.finance.core.domain.usecase._

class CalculatePortfolioImplTest extends AnyFlatSpec with Matchers with MockFactory {

  def mkGetCompanies(companies: List[Company]): GetCompanies[IO] = {
    val getCompanies = mock[GetCompanies[IO]]
    (getCompanies.run _)
      .expects(*)
      .returning(IO.pure(companies))
      .anyNumberOfTimes()
    getCompanies
  }

  def mkGetCompaniesMarketCapitalization(
    companyToMarketCap: Map[Company, MarketCapitalization]
  ): GetCompaniesMarketCapitalization[IO] = {
    val getCompaniesMarketCapitalization = mock[GetCompaniesMarketCapitalization[IO]]
    (getCompaniesMarketCapitalization.run _)
      .expects(*)
      .returning(IO.pure(companyToMarketCap))
      .anyNumberOfTimes()
    getCompaniesMarketCapitalization
  }

  def mkGetCompaniesStocks(
    companiesToStocks: Map[Company, Stock]
  ): GetCompaniesStocks[IO] = {
    val getCompaniesStocks = mock[GetCompaniesStocks[IO]]
    (getCompaniesStocks.run _)
      .expects(*)
      .returning(IO.pure(companiesToStocks))
      .anyNumberOfTimes()
    getCompaniesStocks
  }

  it should s"raise $NotAllCompaniesHaveCapitalizationError" in {
    val companies    = List(Company("AAPL"), Company("AMZN"))
    val getCompanies = mkGetCompanies(companies)

    val getCompaniesMarketCapitalization = mkGetCompaniesMarketCapitalization(Map(
      Company("AMZN") -> MarketCapitalization(100)
    ))
    val getCompaniesStocks = mkGetCompaniesStocks(Map.empty)
    val calculatePortfolio = new CalculatePortfolioImpl(
      getCompanies, getCompaniesMarketCapitalization, getCompaniesStocks
    )
    val params = CalculatePortfolioParams(NasdaqIndex, 1000, None, Nil)
    val portfolio = calculatePortfolio.run(params).unsafeRunSync()

    portfolio shouldBe Left(NotAllCompaniesHaveCapitalizationError(List(Company("AAPL"))))
  }

  it should s"raise $NotAllCompaniesHaveStocksError" in {
    val companies    = List(Company("AAPL"), Company("AMZN"))
    val getCompanies = mkGetCompanies(companies)

    val getCompaniesMarketCapitalization = mkGetCompaniesMarketCapitalization(Map(
      Company("AAPL") -> MarketCapitalization(200),
      Company("AMZN") -> MarketCapitalization(100)
    ))
    val getCompaniesStocks = mkGetCompaniesStocks(Map(
      Company("AAPL") -> Stock(120, 5)
    ))
    val calculatePortfolio = new CalculatePortfolioImpl(
      getCompanies, getCompaniesMarketCapitalization, getCompaniesStocks
    )
    val params = CalculatePortfolioParams(NasdaqIndex, 1000, None, Nil)
    val portfolio = calculatePortfolio.run(params).unsafeRunSync()

    portfolio shouldBe Left(NotAllCompaniesHaveStocksError(List(Company("AMZN"))))
  }

  it should s"raise $NotEnoughMoneyError" in {
    val companies    = List(Company("AAPL"), Company("AMZN"))
    val getCompanies = mkGetCompanies(companies)

    val getCompaniesMarketCapitalization = mkGetCompaniesMarketCapitalization(Map(
      Company("AAPL") -> MarketCapitalization(200),
      Company("AMZN") -> MarketCapitalization(100)
    ))
    val getCompaniesStocks = mkGetCompaniesStocks(Map(
      Company("AAPL") -> Stock(120, 5),
      Company("AMZN") -> Stock(110, 5)
    ))
    val calculatePortfolio = new CalculatePortfolioImpl(
      getCompanies, getCompaniesMarketCapitalization, getCompaniesStocks
    )
    val investmentSum = 50
    val params = CalculatePortfolioParams(NasdaqIndex, investmentSum, None, Nil)
    val portfolio = calculatePortfolio.run(params).unsafeRunSync()

    portfolio shouldBe Left(NotEnoughMoneyError(110))
  }

  it should s"raise $EmptyIndexCompaniesError" in {
    val companies    = Nil
    val getCompanies = mkGetCompanies(companies)

    val getCompaniesMarketCapitalization = mkGetCompaniesMarketCapitalization(Map.empty)
    val getCompaniesStocks = mkGetCompaniesStocks(Map.empty)
    val calculatePortfolio = new CalculatePortfolioImpl(
      getCompanies, getCompaniesMarketCapitalization, getCompaniesStocks
    )
    val params = CalculatePortfolioParams(NasdaqIndex, 1000, None, Nil)
    val portfolio = calculatePortfolio.run(params).unsafeRunSync()

    portfolio shouldBe Left(EmptyIndexCompaniesError(NasdaqIndex))
  }

  it should s"raise $AllCompaniesExcludedError" in {
    val companies    = List(Company("AAPL"), Company("AMZN"))
    val getCompanies = mkGetCompanies(companies)

    val getCompaniesMarketCapitalization = mkGetCompaniesMarketCapitalization(Map.empty)
    val getCompaniesStocks = mkGetCompaniesStocks(Map.empty)
    val calculatePortfolio = new CalculatePortfolioImpl(
      getCompanies, getCompaniesMarketCapitalization, getCompaniesStocks
    )
    val params = CalculatePortfolioParams(NasdaqIndex, 1000, None, List(Company("AAPL"), Company("AMZN")))
    val portfolio = calculatePortfolio.run(params).unsafeRunSync()

    portfolio shouldBe Left(AllCompaniesExcludedError)
  }

  it should s"calculate portfolio with sum not more than investment sum" in {
    val companies    = List(Company("GOOG"), Company("AAPL"), Company("AMZN"))
    val getCompanies = mkGetCompanies(companies)

    val getCompaniesMarketCapitalization = mkGetCompaniesMarketCapitalization(Map(
      Company("AMZN") -> MarketCapitalization(200),
      Company("AAPL") -> MarketCapitalization(300),
      Company("GOOG") -> MarketCapitalization(100)
    ))
    val getCompaniesStocks = mkGetCompaniesStocks(Map(
      Company("AAPL") -> Stock(120, 99),
      Company("AMZN") -> Stock(110, 99),
      Company("GOOG") -> Stock(130, 99)
    ))
    val calculatePortfolio = new CalculatePortfolioImpl(
      getCompanies, getCompaniesMarketCapitalization, getCompaniesStocks
    )
    val investmentSum = BigDecimal(400)
    val params = CalculatePortfolioParams(NasdaqIndex, investmentSum, None, Nil)
    val portfolio = calculatePortfolio.run(params).unsafeRunSync()

    portfolio match {
      case Left(error) => fail(error.message)
      case Right(Portfolio(portfolioStocks)) => portfolioStocks
        .map(_.sum).sum should be <= investmentSum
    }
  }

  it should s"calculate portfolio only for index cap depth" in {
    val companies    = List(Company("AAPL"), Company("AMZN"), Company("GOOG"))
    val getCompanies = mkGetCompanies(companies)

    val getCompaniesMarketCapitalization = mkGetCompaniesMarketCapitalization(Map(
      Company("AMZN") -> MarketCapitalization(200),
      Company("AAPL") -> MarketCapitalization(300),
      Company("GOOG") -> MarketCapitalization(100)
    ))
    val getCompaniesStocks = mkGetCompaniesStocks(Map(
      Company("AAPL") -> Stock(120, 99),
      Company("AMZN") -> Stock(110, 99),
      Company("GOOG") -> Stock(140, 99)
    ))
    val calculatePortfolio = new CalculatePortfolioImpl(
      getCompanies, getCompaniesMarketCapitalization, getCompaniesStocks
    )
    val params = CalculatePortfolioParams(NasdaqIndex, 400, Some(2), Nil)
    val portfolio = calculatePortfolio.run(params).unsafeRunSync()

    portfolio match {
      case Left(error) => fail(error.message)
      case Right(Portfolio(portfolioStocks)) => portfolioStocks
        .map(_.company) should contain theSameElementsAs Set(Company("AAPL"), Company("AMZN"))
    }
  }

  it should s"calculate a balanced portfolio for companies with small capitalization" in {
    val companies    = List(Company("AAPL"), Company("AMZN"), Company("GOOG"))
    val getCompanies = mkGetCompanies(companies)

    val getCompaniesMarketCapitalization = mkGetCompaniesMarketCapitalization(Map(
      Company("AMZN") -> MarketCapitalization(2000),
      Company("AAPL") -> MarketCapitalization(3000),
      Company("GOOG") -> MarketCapitalization(1)
    ))
    val getCompaniesStocks = mkGetCompaniesStocks(Map(
      Company("AAPL") -> Stock(120, 99),
      Company("AMZN") -> Stock(110, 99),
      Company("GOOG") -> Stock(140, 99)
    ))
    val calculatePortfolio = new CalculatePortfolioImpl(
      getCompanies, getCompaniesMarketCapitalization, getCompaniesStocks
    )
    val params = CalculatePortfolioParams(NasdaqIndex, 400, None, Nil)
    val portfolio = calculatePortfolio.run(params).unsafeRunSync()

    portfolio match {
      case Left(error) => fail(error.message)
      case Right(Portfolio(portfolioStocks)) => portfolioStocks
        .map(_.company) should contain theSameElementsAs Set(Company("AAPL"), Company("AMZN"), Company("GOOG"))
    }
  }

  it should s"completely fill portfolio" in {
    val companies    = List(Company("GOOG"), Company("AAPL"), Company("AMZN"))
    val getCompanies = mkGetCompanies(companies)

    val getCompaniesMarketCapitalization = mkGetCompaniesMarketCapitalization(Map(
      Company("AMZN") -> MarketCapitalization(200),
      Company("AAPL") -> MarketCapitalization(300),
      Company("GOOG") -> MarketCapitalization(100)
    ))
    val minStockPrice = BigDecimal(13)
    val getCompaniesStocks = mkGetCompaniesStocks(Map(
      Company("AAPL") -> Stock(120, 99),
      Company("AMZN") -> Stock(110, 99),
      Company("GOOG") -> Stock(minStockPrice, 99)
    ))
    val calculatePortfolio = new CalculatePortfolioImpl(
      getCompanies, getCompaniesMarketCapitalization, getCompaniesStocks
    )
    val investmentSum = BigDecimal(400)
    val params = CalculatePortfolioParams(NasdaqIndex, investmentSum, None, Nil)
    val portfolio = calculatePortfolio.run(params).unsafeRunSync()

    portfolio match {
      case Left(error) => fail(error.message)
      case Right(Portfolio(portfolioStocks)) => portfolioStocks
        .map(_.sum).sum should be >= investmentSum - minStockPrice
    }
  }
}
