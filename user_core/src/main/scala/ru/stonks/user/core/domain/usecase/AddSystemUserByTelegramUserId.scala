package ru.stonks.user.core.domain.usecase

import ru.stonks.entity.user.{SystemUser, TelegramUserId}

trait AddSystemUserByTelegramUserId[F[_]] {
  def run(telegramUserId: TelegramUserId): F[SystemUser]
}
