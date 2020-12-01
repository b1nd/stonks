package ru.stonks.app.component

import cats.effect.{ConcurrentEffect, ContextShift, Resource, Timer}
import org.http4s.HttpRoutes
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder
import ru.stonks.app.config.ServerConfig

import scala.concurrent.ExecutionContext

class AppServer[F[_] : ConcurrentEffect : ContextShift : Timer](serverConfig: ServerConfig) {
  def run: Resource[F, Server[F]] = {
    val routes = HttpRoutes.empty

    server(routes)
  }

  private[this] def server(routes: HttpRoutes[F]): Resource[F, Server[F]] = {
    import org.http4s.implicits._

    BlazeServerBuilder[F](ExecutionContext.global)
      .bindHttp(serverConfig.port, serverConfig.host)
      .withHttpApp(routes.orNotFound)
      .resource
  }
}

object AppServer {
  def apply[F[_]](serverConfig: ServerConfig)(implicit
    concurrentEffect: ConcurrentEffect[F],
    contextShift: ContextShift[F],
    timer: Timer[F]
  ): AppServer[F] = new AppServer(serverConfig)
}
