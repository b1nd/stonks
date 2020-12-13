package ru.stonks.user.core.domain.usecase

import ru.stonks.entity.user.{SystemUser, TelegramUserId}

trait GetSystemUserFromTelegramUserId[F[_]] {
  def run(telegramUserId: TelegramUserId): F[Option[SystemUser]]
}
