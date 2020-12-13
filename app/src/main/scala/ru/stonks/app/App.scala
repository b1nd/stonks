package ru.stonks.app

import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.syntax.parallel._
import ru.stonks.app.component._

object App extends IOApp {

  type F[+A] = IO[A]

  override def run(args: List[String]): IO[ExitCode] = {
    val app = for {
      config <- AppConfig.run[F]
      dbToClient <- (
          AppDatabase[F](config.db).run,
          AppClient[F](config.client).run
        ).parTupled
      (db, client) = dbToClient
      botApi <- AppTelegramBotApi[F](client, config.telegramBot).run
      modules = AppModules(config.financeApi, db, client, botApi)
      _ <- AppScheduling[F](config.scheduling, modules).run
      _ <- Resource.liftF(modules.botModule.telegramLongPollBot.start())
    } yield ()

    app
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }

}
