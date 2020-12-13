package ru.stonks.bot.core.domain.usecase

import ru.stonks.entity.user.SystemUser
import ru.stonks.entity.bot._

trait SaveUserBotCommandWithInput[F[_]] {
  def run(
    systemUser: SystemUser,
    botPlatform: BotPlatform,
    botCommandWithInput: BotCommandWithInput
  ): F[Boolean]
}
