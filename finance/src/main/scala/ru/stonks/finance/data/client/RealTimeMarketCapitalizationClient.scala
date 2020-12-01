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
    uri <- createUri(company)
    request = mkRequest(uri)
    response <- fetchRequest(request)
    maybeMarketCapitalization = handleResponse(response)
  } yield maybeMarketCapitalization

  private def createUri(company: Company) = for {
    baseUri <- F.fromEither[Uri](Uri.fromString(
      s"${financeApiClientCredentials.baseUrl}/market-capitalization/${company.ticker}"))
    uri = baseUri.withQueryParam("apikey", financeApiClientCredentials.key)
  } yield uri

  private def mkRequest(uri: Uri) =
    Request[F]()
      .withMethod(Method.GET)
      .withUri(uri)

  private def fetchRequest(request: Request[F]) =
    client.fetchAs(request)(jsonOf[F, List[MarketCapitalizationResponse]])

  private def handleResponse(response: List[MarketCapitalizationResponse]) =
    response
      .map(r => MarketCapitalization(r.marketCap))
      .headOption
}
