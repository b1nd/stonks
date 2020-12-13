package ru.stonks.bot.core.domain.usecase

import ru.stonks.bot.core.domain.entity._
import ru.stonks.entity.bot.{BotCommand, BotPlatform}
import ru.stonks.entity.user.SystemUser

trait ParseBotCommand[F[_]] {
  def run(
    command: String,
    systemUser: SystemUser,
    botPlatform: BotPlatform
  ): F[Either[BotCommandParseError, BotCommand]]
}
