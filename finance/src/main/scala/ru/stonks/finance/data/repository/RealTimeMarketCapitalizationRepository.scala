package ru.stonks.finance.data.repository

import cats.effect.{ContextShift, Sync}
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.{Applicative, MonadError}
import org.http4s.circe.jsonOf
import org.http4s.client.Client
import org.http4s.{Method, Request, Uri}
import ru.stonks.entity.finance.{Company, MarketCapitalization}
import ru.stonks.finance.data.dto.MarketCapitalizationResponse
import ru.stonks.finance.domain.repository.MarketCapitalizationRepository

class RealTimeMarketCapitalizationRepository[F[_] : Sync : ContextShift](
  financeApiBaseUrl: String,
  financeApiKey: String,
  client: Client[F])(implicit
  F: MonadError[F, Throwable]
) extends MarketCapitalizationRepository[F] {

  override def get(company: Company): F[Option[MarketCapitalization]] = for {
    uri <- createUri(company)
    request <- mkRequest(uri)
    response <- fetchRequest(request)
    maybeMarketCapitalization <- handleResponse(response)
  } yield maybeMarketCapitalization

  private def createUri(company: Company) = for {
    baseUri <- F.fromEither[Uri](Uri.fromString(s"$financeApiBaseUrl/market-capitalization/${company.ticker}"))
    uri <- F.pure(baseUri.withQueryParam("apikey", financeApiKey))
  } yield uri

  private def mkRequest(uri: Uri) = Applicative[F].pure {
    Request[F]()
      .withMethod(Method.GET)
      .withUri(uri)
  }

  private def fetchRequest(request: Request[F]) =
    client.fetchAs(request)(jsonOf[F, List[MarketCapitalizationResponse]])

  private def handleResponse(response: Seq[MarketCapitalizationResponse]) = Applicative[F].pure {
    response
      .map(r => MarketCapitalization(r.marketCap))
      .headOption
  }
}
