package ru.stonks.finance.data.repository

import cats.effect.{ContextShift, Sync}
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.{Applicative, MonadError}
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.{Method, Request, Uri}
import ru.stonks.entity.finance.{Company, Stock}
import ru.stonks.finance.data.dto.StockResponse
import ru.stonks.finance.domain.repository.StockRepository

class RealTimeStockRepository[F[_] : Sync : ContextShift](
  financeApiBaseUrl: String,
  financeApiKey: String,
  client: Client[F])(implicit
  F: MonadError[F, Throwable]
) extends StockRepository[F] {

  override def findAllByCompanies(companies: List[Company]): F[Map[Company, Stock]] = for {
    uri <- createUri(companies)
    request <- mkRequest(uri)
    response <- fetchRequest(request)
    stocksByCompanies <- handleResponse(response)
  } yield stocksByCompanies

  private def createUri(companies: List[Company]) = for {
    baseUri <- F.fromEither[Uri](Uri.fromString(s"$financeApiBaseUrl/quote/${companies.map(_.ticker).mkString(",")}"))
    uri <- F.pure(baseUri.withQueryParam("apikey", financeApiKey))
  } yield uri

  private def mkRequest(uri: Uri) = Applicative[F].pure {
    Request[F]()
      .withMethod(Method.GET)
      .withUri(uri)
  }

  private def fetchRequest(request: Request[F]) =
    client.fetchAs(request)(jsonOf[F, List[StockResponse]])

  private def handleResponse(response: List[StockResponse]) = Applicative[F].pure {
    response
      .map(r => (Company(r.symbol), Stock(r.price, r.volume)))
      .toMap
  }
}
