package ru.stonks.nasdaq.data.client

import cats.MonadError
import cats.effect.{ContextShift, Sync}
import cats.syntax.flatMap._
import cats.syntax.functor._
import org.http4s.circe.jsonOf
import org.http4s.client.Client
import org.http4s.{Method, Request, Uri}
import ru.stonks.entity.finance.Company
import ru.stonks.nasdaq.data.dto.NasdaqCompanyResponse
import ru.stonks.nasdaq.domain.client.NasdaqCompaniesClient

class RealTimeNasdaqCompaniesClient[F[_] : Sync : ContextShift](
  nasdaqApiClientCredentials: NasdaqApiClientCredentials,
  client: Client[F])(implicit
  F: MonadError[F, Throwable]
) extends NasdaqCompaniesClient[F] {

  override def getAll: F[List[Company]] = for {
    uri <- createUri
    request = mkRequest(uri)
    response <- fetchRequest(request)
    nasdaqCompanies = handleResponse(response)
  } yield nasdaqCompanies

  private def createUri = for {
    baseUri <- F.fromEither[Uri](Uri.fromString(
      s"${nasdaqApiClientCredentials.baseUrl}/nasdaq_constituent"))
    uri = baseUri.withQueryParam("apikey", nasdaqApiClientCredentials.key)
  } yield uri

  private def mkRequest(uri: Uri) =
    Request[F]()
      .withMethod(Method.GET)
      .withUri(uri)

  private def fetchRequest(request: Request[F]) =
    client.fetchAs(request)(jsonOf[F, List[NasdaqCompanyResponse]])

  private def handleResponse(response: List[NasdaqCompanyResponse]) =
    response.map(r => Company(r.symbol.toUpperCase))
}
