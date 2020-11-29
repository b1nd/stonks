package ru.stonks.app.component

import cats.effect.{ConcurrentEffect, Resource}
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.middleware.Logger
import ru.stonks.app.config.ClientConfig

import scala.concurrent.ExecutionContext

class AppClient[F[_] : ConcurrentEffect](clientConfig: ClientConfig) {
  def run: Resource[F, Client[F]] = {
    BlazeClientBuilder[F](ExecutionContext.global)
      .resource
      .map(client => Logger(
        logHeaders = clientConfig.logHeaders,
        logBody = clientConfig.logBody
      )(client))
  }
}

object AppClient {
  def apply[F[_]](clientConfig: ClientConfig)(implicit
    concurrentEffect: ConcurrentEffect[F]
  ): AppClient[F] = new AppClient(clientConfig)
}
