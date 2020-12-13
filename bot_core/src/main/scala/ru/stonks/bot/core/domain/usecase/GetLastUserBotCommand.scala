package ru.stonks.bot.core.domain.usecase

import ru.stonks.entity.bot.{BotCommand, BotPlatform}
import ru.stonks.entity.user.SystemUser

trait GetLastUserBotCommand[F[_]] {
  def run(systemUser: SystemUser, botPlatform: BotPlatform): F[Option[BotCommand]]
}
