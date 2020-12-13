package ru.stonks.app.component

import cats.effect.{ConcurrentEffect, ContextShift, Resource, Sync}
import org.http4s.client.Client
import ru.stonks.app.config.TelegramBotConfig
import telegramium.bots.client.Methods
import telegramium.bots.high.{Api, BotApi}

class AppTelegramBotApi[F[_] : ConcurrentEffect : ContextShift](
  client: Client[F],
  telegramBotConfig: TelegramBotConfig)(implicit
  F: Sync[F]
) extends Methods {

  def run: Resource[F, Api[F]] = {
    Resource.pure(createBotBackend(client, telegramBotConfig.token))
  }

  private def createBotBackend(http: Client[F], token: String) =
    BotApi(http, baseUrl = s"https://api.telegram.org/bot$token")
}

object AppTelegramBotApi {
  def apply[F[_]](
    client: Client[F],
    telegramBotConfig: TelegramBotConfig)(implicit
    concurrentEffect: ConcurrentEffect[F],
    contextShift: ContextShift[F]
  ): AppTelegramBotApi[F] = new AppTelegramBotApi(client, telegramBotConfig)
}
