package ru.stonks.user.core.domain.dto

import ru.stonks.entity.user.{SystemUserId, TelegramUserId}

case class TelegramUserDto(
  telegramUserId: TelegramUserId,
  systemUserId: SystemUserId
)
