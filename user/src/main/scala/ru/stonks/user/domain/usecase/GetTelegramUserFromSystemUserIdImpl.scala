package ru.stonks.user.domain.usecase

import ru.stonks.entity.user.{SystemUserId, TelegramUser}
import ru.stonks.user.core.domain.usecase.GetTelegramUserFromSystemUserId
import ru.stonks.user.domain.repository.TelegramUserRepository

class GetTelegramUserFromSystemUserIdImpl[F[_]](
  telegramUserRepository: TelegramUserRepository[F]
) extends GetTelegramUserFromSystemUserId[F] {

  override def run(systemUserId: SystemUserId): F[Option[TelegramUser]] = {
    telegramUserRepository.findBySystemUserId(systemUserId)
  }
}
