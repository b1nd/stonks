package ru.stonks.bot.domain.usecase

import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import ru.stonks.bot.core.domain.usecase.ResetAllBotUserCommands
import ru.stonks.bot.domain.repository.TelegramUserBotCommandRepository
import ru.stonks.entity.bot.{BotPlatform, TelegramBot}
import ru.stonks.entity.user.SystemUser
import ru.stonks.user.core.domain.usecase.GetTelegramUserFromSystemUserId

class ResetAllBotUserCommandsImpl[F[_]](
  getTelegramUserFromSystemUserId: GetTelegramUserFromSystemUserId[F],
  telegramUserBotCommandRepository: TelegramUserBotCommandRepository[F])(implicit
  F: Sync[F]
) extends ResetAllBotUserCommands[F] {

  override def run(systemUser: SystemUser, botPlatform: BotPlatform): F[Boolean] = botPlatform match {
    case TelegramBot => for {
      maybeTelegramUser <- getTelegramUserFromSystemUserId.run(systemUser.id)
      isReset           <- maybeTelegramUser match {
        case Some(telegramUser) => telegramUserBotCommandRepository.deleteAllByUser(telegramUser.id)
        case None => F.pure(false)
      }
    } yield isReset
  }
}
