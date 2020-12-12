package ru.stonks.user.core.domain.usecase

import ru.stonks.entity.user.{SystemUser, TelegramUserId}

trait GetOrAddSystemUserByTelegramUserId[F[_]] {
  def run(telegramUserId: TelegramUserId): F[SystemUser]
}
