package ru.stonks.algorithm.domain.usecase

import cats.data.EitherT
import cats.effect.Sync
import ru.stonks.algorithm.core.domain.usecase.CalculatePortfolio
import ru.stonks.entity.algorithm._
import ru.stonks.entity.finance._
import ru.stonks.finance.core.domain.usecase._

import scala.annotation.tailrec

class CalculatePortfolioImpl[F[_] : Sync](
  getCompanies: GetCompanies[F],
  getCompaniesMarketCapitalization: GetCompaniesMarketCapitalization[F],
  getCompaniesStocks: GetCompaniesStocks[F]
) extends CalculatePortfolio[F] {

  override def run(params: CalculatePortfolioParams): F[Either[CalculatePortfolioError, Portfolio]] = (for {
    companies        <- getCompanies(params)
    companyToCap     <- EitherT.right(getCompaniesMarketCapitalization.run(companies))
    orderedCompanies <- orderCompanies(companies, companyToCap, params)
    companyToStock   <- getCompaniesStocks(orderedCompanies)
    portfolioSum     = params.dollarsSum
    minStockPrice    <- getMinimumStockPrice(companyToStock, portfolioSum)
    sharedPortfolio  = getSharedPortfolioStocks(orderedCompanies, companyToCap, companyToStock, portfolioSum)
    portfolio        = fillPortfolio(sharedPortfolio, companyToStock, minStockPrice, portfolioSum)
  } yield portfolio).value

  private def getCompanies(
    params: CalculatePortfolioParams
  ): EitherT[F, CalculatePortfolioError, List[Company]] = for {
    indexCompanies    <- EitherT.right(getCompanies.run(params.marketIndex))
    _                 <- EitherT.cond[F][CalculatePortfolioError, List[Company]](
                           indexCompanies.nonEmpty,
                           indexCompanies,
                           EmptyIndexCompaniesError(params.marketIndex))
    filteredCompanies = indexCompanies.diff(params.excludeCompanies)
    companiesOrError  <- EitherT.cond[F][CalculatePortfolioError, List[Company]](
                           filteredCompanies.nonEmpty,
                           filteredCompanies,
                           AllCompaniesExcludedError)
  } yield companiesOrError

  private def getCompaniesStocks(
    companies: List[Company]
  ): EitherT[F, CalculatePortfolioError, Map[Company, Stock]] = for {
    companyToStock         <- EitherT.right(getCompaniesStocks.run(companies))
    companiesWithoutStocks = companies.diff(companyToStock.keys.toSeq)
    companyToStockOrError  <- EitherT.cond[F][CalculatePortfolioError, Map[Company, Stock]](
                                companiesWithoutStocks.isEmpty,
                                companyToStock,
                                NotAllCompaniesHaveStocksError(companiesWithoutStocks))
  } yield companyToStockOrError

  private def orderCompanies(
    companies: List[Company],
    companyToCap: Map[Company, MarketCapitalization],
    params: CalculatePortfolioParams
  ): EitherT[F, CalculatePortfolioError, List[Company]] = for {
    companiesWithoutCap    <- EitherT.rightT(companies.diff(companyToCap.keys.toSeq))
    companiesThatHaveCap   <- EitherT.cond[F][CalculatePortfolioError, List[Company]](
                                companiesWithoutCap.isEmpty,
                                companies,
                                NotAllCompaniesHaveCapitalizationError(companiesWithoutCap))
    orderedCompanies       = companiesThatHaveCap.sortBy(company => -companyToCap(company).dollars)
    indexCapDepthCompanies = params.indexCapDepth
                               .map(orderedCompanies.take)
                               .getOrElse(orderedCompanies)
  } yield indexCapDepthCompanies

  private def getMinimumStockPrice(
    companyToStock: Map[Company, Stock],
    dollarsSum: BigDecimal
  ): EitherT[F, CalculatePortfolioError, BigDecimal] = {
    val minStockPrice = companyToStock.map { case (_, stock) => stock.dollarsPrice }.min
    EitherT.cond(dollarsSum >= minStockPrice,
      minStockPrice,
      NotEnoughMoneyError(minStockPrice))
  }

  private def getSharedPortfolioStocks(
    companies: List[Company],
    companyToCap: Map[Company, MarketCapitalization],
    companyToStock: Map[Company, Stock],
    dollarsSum: BigDecimal
  ): List[PortfolioCompanyStock] = {
    val totalCap = companies.map(companyToCap).map(_.dollars).sum
    companies.map { company =>
      val estimatedShare = companyToCap(company).dollars / totalCap * dollarsSum
      val stockPrice     = companyToStock(company).dollarsPrice
      val stockCount     = (estimatedShare / stockPrice).toLong
      val stocksSum      = stockCount * stockPrice
      PortfolioCompanyStock(company, stockCount, stocksSum)
    }
  }

  private def fillPortfolio(
    sharedPortfolioStocks: List[PortfolioCompanyStock],
    companyToStock: Map[Company, Stock],
    minStockPrice: BigDecimal,
    dollarsSum: BigDecimal
  ): Portfolio = {

    def fillStocksByOne(
      portfolioStocks: List[PortfolioCompanyStock],
      remainingSum: BigDecimal,
      acc: List[PortfolioCompanyStock] = Nil
    ): (List[PortfolioCompanyStock], BigDecimal) = portfolioStocks.foldLeft((acc, remainingSum)) {
      case ((acc, remainingSum), stock@PortfolioCompanyStock(company, count, stockSum)) =>
        if (remainingSum < minStockPrice) {
          (stock :: acc, remainingSum)
        } else if (remainingSum >= companyToStock(company).dollarsPrice) {
          val price          = companyToStock(company).dollarsPrice
          val portfolioStock = stock.copy(
            count = count + 1,
            sum = stockSum + price)
          (portfolioStock :: acc, remainingSum - price)
        } else {
          (stock :: acc, remainingSum)
        }
    }

    @tailrec def fillUntilFull(
      portfolioStocks: List[PortfolioCompanyStock],
      dollarsSum: BigDecimal
    ): List[PortfolioCompanyStock] = if (dollarsSum >= minStockPrice) {
      val (currentPortfolio, remainingSum) = fillStocksByOne(portfolioStocks, dollarsSum)
      fillUntilFull(currentPortfolio, remainingSum)
    } else {
      portfolioStocks
    }

    val remainingSumAfterSharedPortfolio = dollarsSum - sharedPortfolioStocks.map(_.sum).sum
    val (zeroStocks, otherStocks)        = sharedPortfolioStocks.partition(_.count == 0)
    val (filledZeroStocks, remainingSum) = fillStocksByOne(zeroStocks, remainingSumAfterSharedPortfolio)
    val allStocks                        = otherStocks ::: filledZeroStocks
    val filledPortfolioStocks            = fillUntilFull(allStocks, remainingSum)

    Portfolio(filledPortfolioStocks)
  }
}
