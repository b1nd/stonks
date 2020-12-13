package ru.stonks.user.domain.usecase

import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import ru.stonks.entity.user.{SystemUser, TelegramUserId}
import ru.stonks.user.core.domain.dto.{SystemUserDto, TelegramUserDto}
import ru.stonks.user.core.domain.usecase.AddSystemUserByTelegramUserId
import ru.stonks.user.domain.repository.{SystemUserRepository, TelegramUserRepository}

class AddSystemUserByTelegramUserIdImpl[F[_] : Sync](
  systemUserRepository: SystemUserRepository[F],
  telegramUserRepository: TelegramUserRepository[F]
) extends AddSystemUserByTelegramUserId[F] {

  override def run(telegramUserId: TelegramUserId): F[SystemUser] = for {
    systemUser <- systemUserRepository.save(SystemUserDto(isTelegramUser = true))
    _          <- telegramUserRepository.save(TelegramUserDto(telegramUserId, systemUser.id))
  } yield systemUser
}
