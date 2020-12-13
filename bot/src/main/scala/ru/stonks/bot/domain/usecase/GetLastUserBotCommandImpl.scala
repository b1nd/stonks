package ru.stonks.bot.domain.usecase

import cats.data.OptionT
import cats.effect.Sync
import ru.stonks.bot.core.domain.usecase.GetLastUserBotCommand
import ru.stonks.bot.domain.repository.TelegramUserBotCommandRepository
import ru.stonks.entity.bot.{BotCommand, BotPlatform, TelegramBot}
import ru.stonks.entity.user.SystemUser
import ru.stonks.user.core.domain.usecase.GetTelegramUserFromSystemUserId

class GetLastUserBotCommandImpl[F[_] : Sync](
  getTelegramUserFromSystemUserId: GetTelegramUserFromSystemUserId[F],
  telegramUserBotCommandRepository: TelegramUserBotCommandRepository[F]
) extends GetLastUserBotCommand[F] {

  override def run(systemUser: SystemUser, botPlatform: BotPlatform): F[Option[BotCommand]] = (botPlatform match {
    case TelegramBot => for {
      telegramUser    <- OptionT(getTelegramUserFromSystemUserId.run(systemUser.id))
      maybeBotCommand <- OptionT(telegramUserBotCommandRepository.findLastUserBotCommand(telegramUser.id))
    } yield maybeBotCommand
  }).value
}
