package ru.stonks.finance.data.client

import cats.MonadError
import cats.effect.{ContextShift, Sync}
import cats.syntax.flatMap._
import cats.syntax.functor._
import org.http4s.circe.jsonOf
import org.http4s.client.Client
import org.http4s.{Method, Request, Uri}
import ru.stonks.entity.finance.{Company, MarketCapitalization}
import ru.stonks.finance.data.dto.MarketCapitalizationResponse
import ru.stonks.finance.domain.client.MarketCapitalizationClient

class RealTimeMarketCapitalizationClient[F[_] : Sync : ContextShift](
  financeApiClientCredentials: FinanceApiClientCredentials,
  client: Client[F])(implicit
  F: MonadError[F, Throwable]
) extends MarketCapitalizationClient[F] {

  override def get(company: Company): F[Option[MarketCapitalization]] = for {
    uri <- singleMarketCapUri(company)
    request = mkRequest(uri)
    response <- fetchRequest(request)
    maybeMarketCapitalization = handleSingleCapResponse(response)
  } yield maybeMarketCapitalization

  override def getAll(companies: List[Company]): F[List[(Company, MarketCapitalization)]] = for {
    uri <- multipleMarketCapUri(companies)
    request = mkRequest(uri)
    response <- fetchRequest(request)
    companiesMarketCapitalization = handleMultiCapResponse(response)
  } yield companiesMarketCapitalization

  private def singleMarketCapUri(company: Company) = for {
    baseUri <- uriFromString(
      s"${financeApiClientCredentials.baseUrl}/market-capitalization/${company.ticker}")
    uri = withApiKey(baseUri)
  } yield uri

  private def handleSingleCapResponse(response: List[MarketCapitalizationResponse]) =
    response
      .map(r => MarketCapitalization(r.marketCap))
      .headOption

  private def multipleMarketCapUri(companies: List[Company]) = for {
    baseUri <- uriFromString(
      s"${financeApiClientCredentials.baseUrl}/quote/${companies.map(_.ticker).mkString(",")}")
    uri = withApiKey(baseUri)
  } yield uri

  private def handleMultiCapResponse(response: List[MarketCapitalizationResponse]) =
    response.map(r => (Company(r.symbol.toUpperCase), MarketCapitalization(r.marketCap)))

  private def uriFromString(stringUri: String) =
    F.fromEither[Uri](Uri.fromString(stringUri))

  private def withApiKey(uri: Uri) =
    uri.withQueryParam("apikey", financeApiClientCredentials.key)

  private def mkRequest(uri: Uri) =
    Request[F]()
      .withMethod(Method.GET)
      .withUri(uri)

  private def fetchRequest(request: Request[F]) =
    client.fetchAs(request)(jsonOf[F, List[MarketCapitalizationResponse]])
}
