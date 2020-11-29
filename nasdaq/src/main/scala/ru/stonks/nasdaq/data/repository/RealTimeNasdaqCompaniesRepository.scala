package ru.stonks.nasdaq.data.repository

import cats.effect.{ContextShift, Sync}
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.{Applicative, MonadError}
import org.http4s.circe.jsonOf
import org.http4s.client.Client
import org.http4s.{Method, Request, Uri}
import ru.stonks.entity.finance.Company
import ru.stonks.nasdaq.data.dto.NasdaqCompanyResponse
import ru.stonks.nasdaq.domain.repository.NasdaqCompaniesRepository

class RealTimeNasdaqCompaniesRepository[F[_] : Sync : ContextShift](
  financeApiBaseUrl: String,
  financeApiKey: String,
  client: Client[F])(implicit
  F: MonadError[F, Throwable]
) extends NasdaqCompaniesRepository[F] {

  override def findAll: F[List[Company]] = for {
    uri <- createUri
    request <- mkRequest(uri)
    response <- fetchRequest(request)
    nasdaqCompanies <- handleResponse(response)
  } yield nasdaqCompanies

  private def createUri = for {
    baseUri <- F.fromEither[Uri](Uri.fromString(s"$financeApiBaseUrl/nasdaq_constituent"))
    uri <- F.pure(baseUri.withQueryParam("apikey", financeApiKey))
  } yield uri

  private def mkRequest(uri: Uri) = Applicative[F].pure {
    Request[F]()
      .withMethod(Method.GET)
      .withUri(uri)
  }

  private def fetchRequest(request: Request[F]) =
    client.fetchAs(request)(jsonOf[F, List[NasdaqCompanyResponse]])

  private def handleResponse(response: List[NasdaqCompanyResponse]) = Applicative[F].pure {
    response.map(r => Company(r.symbol))
  }
}
