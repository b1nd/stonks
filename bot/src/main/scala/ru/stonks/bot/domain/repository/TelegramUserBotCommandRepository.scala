package ru.stonks.bot.domain.repository

import ru.stonks.entity.bot.{BotCommand, BotCommandWithInput, BotUserCommand}
import ru.stonks.entity.user.TelegramUserId

trait TelegramUserBotCommandRepository[F[_]] {
  def findLastUserBotCommand(telegramUserId: TelegramUserId): F[Option[BotCommand]]
  def findByBotCommand(telegramUserId: TelegramUserId, botCommand: BotCommand): F[Option[BotUserCommand]]
  def findAllByUser(telegramUserId: TelegramUserId): F[List[BotUserCommand]]
  def saveUserBotCommandWithInput(telegramUserId: TelegramUserId, botCommandWithInput: BotCommandWithInput): F[BotUserCommand]
  def deleteAllByUser(telegramUserId: TelegramUserId): F[Boolean]
}
