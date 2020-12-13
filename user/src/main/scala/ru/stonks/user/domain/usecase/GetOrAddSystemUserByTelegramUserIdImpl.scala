package ru.stonks.user.domain.usecase

import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import ru.stonks.entity.user.{SystemUser, TelegramUserId}
import ru.stonks.user.core.domain.usecase._

class GetOrAddSystemUserByTelegramUserIdImpl[F[_]](
  getSystemUserFromTelegramUserId: GetSystemUserFromTelegramUserId[F],
  addSystemUserByTelegramUserId: AddSystemUserByTelegramUserId[F])(implicit
  F: Sync[F]
) extends GetOrAddSystemUserByTelegramUserId[F] {

  override def run(telegramUserId: TelegramUserId): F[SystemUser] = for {
    maybeSystemUser <- getSystemUserFromTelegramUserId.run(telegramUserId)
    systemUser      <- maybeSystemUser.map(F.pure)
                         .getOrElse(addSystemUserByTelegramUserId.run(telegramUserId))
  } yield systemUser
}
