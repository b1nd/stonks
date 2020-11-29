package ru.stonks.app

import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.syntax.parallel._
import ru.stonks.app.component._

object App extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val app = for {
      config <- AppConfig.run[IO]
      dbToClient <- (
        AppDatabase[IO](config.db).run,
        AppClient[IO](config.client).run
        ).parTupled
      _ <- Resource.liftF(IO.pure(dbToClient)).map { case (db, client) =>
        AppModules(config.financeApi, db, client)
      }
      _ <- AppServer[IO](config.server).run
    } yield ()

    app
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }

}
