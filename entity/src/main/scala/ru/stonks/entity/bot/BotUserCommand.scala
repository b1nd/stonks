package ru.stonks.entity.bot

import ru.stonks.entity.user.TelegramUserId

case class BotUserCommand(
  id: Long,
  telegramUserId: TelegramUserId,
  command: BotCommand,
  input: Option[String]
)
