package ru.stonks.user.domain.repository

import ru.stonks.entity.user.{SystemUserId, TelegramUser, TelegramUserId}
import ru.stonks.user.core.domain.dto.TelegramUserDto

trait TelegramUserRepository[F[_]] {
  def save(telegramUserDto: TelegramUserDto): F[TelegramUser]
  def find(telegramUserId: TelegramUserId): F[Option[TelegramUser]]
  def findBySystemUserId(systemUserId: SystemUserId): F[Option[TelegramUser]]
}
