package ru.stonks.user.domain.usecase

import cats.data.OptionT
import cats.effect.Sync
import ru.stonks.entity.user.{SystemUser, TelegramUserId}
import ru.stonks.user.core.domain.usecase.GetSystemUserFromTelegramUserId
import ru.stonks.user.domain.repository.{SystemUserRepository, TelegramUserRepository}

class GetSystemUserFromTelegramUserIdImpl[F[_] : Sync](
  telegramUserRepository: TelegramUserRepository[F],
  systemUserRepository: SystemUserRepository[F]
) extends GetSystemUserFromTelegramUserId[F] {

  override def run(telegramUserId: TelegramUserId): F[Option[SystemUser]] = (for {
    telegramUser <- OptionT(telegramUserRepository.find(telegramUserId))
    systemUser   <- OptionT(systemUserRepository.find(telegramUser.systemUserId))
  } yield systemUser).value
}
