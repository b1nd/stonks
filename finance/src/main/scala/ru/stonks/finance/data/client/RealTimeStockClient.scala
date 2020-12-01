package ru.stonks.finance.data.client

import cats.MonadError
import cats.effect.{ContextShift, Sync}
import cats.syntax.flatMap._
import cats.syntax.functor._
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.{Method, Request, Uri}
import ru.stonks.entity.finance.{Company, Stock}
import ru.stonks.finance.data.dto.StockResponse
import ru.stonks.finance.domain.client.StockClient

class RealTimeStockClient[F[_] : Sync : ContextShift](
  financeApiClientCredentials: FinanceApiClientCredentials,
  client: Client[F])(implicit
  F: MonadError[F, Throwable]
) extends StockClient[F] {

  override def getAllByCompanies(companies: List[Company]): F[Map[Company, Stock]] = for {
    uri <- createUri(companies)
    request = mkRequest(uri)
    response <- fetchRequest(request)
    stocksByCompanies = handleResponse(response)
  } yield stocksByCompanies

  private def createUri(companies: List[Company]) = for {
    baseUri <- F.fromEither[Uri](Uri.fromString(
      s"${financeApiClientCredentials.baseUrl}/quote/${companies.map(_.ticker).mkString(",")}"))
    uri = baseUri.withQueryParam("apikey", financeApiClientCredentials.key)
  } yield uri

  private def mkRequest(uri: Uri) =
    Request[F]()
      .withMethod(Method.GET)
      .withUri(uri)

  private def fetchRequest(request: Request[F]) =
    client.fetchAs(request)(jsonOf[F, List[StockResponse]])

  private def handleResponse(response: List[StockResponse]) =
    response
      .map(r => (Company(r.symbol), Stock(r.price, r.volume)))
      .toMap
}
