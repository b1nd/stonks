package ru.stonks.bot.domain.usecase

import cats.data.OptionT
import cats.effect.Sync
import ru.stonks.bot.core.domain.usecase.SaveUserBotCommandWithInput
import ru.stonks.bot.domain.repository.TelegramUserBotCommandRepository
import ru.stonks.entity.bot.{BotCommandWithInput, BotPlatform, TelegramBot}
import ru.stonks.entity.user.SystemUser
import ru.stonks.user.core.domain.usecase.GetTelegramUserFromSystemUserId

class SaveUserBotCommandWithInputImpl[F[_] : Sync](
  getTelegramUserFromSystemUserId: GetTelegramUserFromSystemUserId[F],
  telegramUserBotCommandRepository: TelegramUserBotCommandRepository[F]
) extends SaveUserBotCommandWithInput[F] {

  override def run(
    systemUser: SystemUser,
    botPlatform: BotPlatform,
    botCommandWithInput: BotCommandWithInput
  ): F[Boolean] = (botPlatform match {
    case TelegramBot => for {
      telegramUser    <- OptionT(getTelegramUserFromSystemUserId.run(systemUser.id))
      maybeBotCommand <- OptionT.liftF {
        telegramUserBotCommandRepository.saveUserBotCommandWithInput(telegramUser.id, botCommandWithInput)
      }
    } yield maybeBotCommand
  }).isDefined
}
