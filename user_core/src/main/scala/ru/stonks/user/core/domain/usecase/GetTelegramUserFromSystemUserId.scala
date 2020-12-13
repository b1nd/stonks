package ru.stonks.user.core.domain.usecase

import ru.stonks.entity.user._

trait GetTelegramUserFromSystemUserId[F[_]] {
  def run(systemUserId: SystemUserId): F[Option[TelegramUser]]
}
