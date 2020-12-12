package ru.stonks.bot.domain.usecase

import cats.data.OptionT
import cats.effect.Sync
import ru.stonks.bot.core.domain.usecase.GetBotUserCommandByBotCommand
import ru.stonks.bot.domain.repository.TelegramUserBotCommandRepository
import ru.stonks.entity.bot.{BotCommand, BotPlatform, BotUserCommand, TelegramBot}
import ru.stonks.entity.user.SystemUser
import ru.stonks.user.core.domain.usecase.GetTelegramUserFromSystemUserId

class GetBotUserCommandByBotCommandImpl[F[_] : Sync](
  getTelegramUserFromSystemUserId: GetTelegramUserFromSystemUserId[F],
  telegramUserBotCommandRepository: TelegramUserBotCommandRepository[F]
) extends GetBotUserCommandByBotCommand[F] {

  override def run(
    botCommand: BotCommand,
    systemUser: SystemUser,
    botPlatform: BotPlatform
  ): F[Option[BotUserCommand]] = (botPlatform match {
    case TelegramBot => for {
      telegramUser        <- OptionT(getTelegramUserFromSystemUserId.run(systemUser.id))
      maybeBotUserCommand <- OptionT(telegramUserBotCommandRepository.findByBotCommand(telegramUser.id, botCommand))
    } yield maybeBotUserCommand
  }).value
}
