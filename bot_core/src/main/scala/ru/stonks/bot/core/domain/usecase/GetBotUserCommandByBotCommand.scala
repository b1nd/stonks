package ru.stonks.bot.core.domain.usecase

import ru.stonks.entity.bot.{BotCommand, BotPlatform, BotUserCommand}
import ru.stonks.entity.user.SystemUser

trait GetBotUserCommandByBotCommand[F[_]] {
  def run(
    botCommand: BotCommand,
    systemUser: SystemUser,
    botPlatform: BotPlatform
  ): F[Option[BotUserCommand]]
}
