package ru.stonks.app

import cats.effect.{ExitCode, IO, IOApp}

object App extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val app = for {
      config <- AppConfig.run[IO]
      _ <- AppServer[IO](config.server).run
    } yield ()

    app
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }

}
