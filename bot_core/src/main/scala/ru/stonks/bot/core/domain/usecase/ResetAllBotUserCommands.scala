package ru.stonks.bot.core.domain.usecase

import ru.stonks.entity.bot.BotPlatform
import ru.stonks.entity.user.SystemUser

trait ResetAllBotUserCommands[F[_]] {
  def run(systemUser: SystemUser, botPlatform: BotPlatform): F[Boolean]
}
