package ru.stonks.user.data.repository

import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor.Transactor
import ru.stonks.entity.user.{SystemUser, SystemUserId}
import ru.stonks.user.core.domain.dto.SystemUserDto
import ru.stonks.user.domain.repository.SystemUserRepository

class DoobieSystemUserRepository[F[_] : Sync](
  transactor: Transactor[F]
) extends SystemUserRepository[F] {

  override def save(systemUserDto: SystemUserDto): F[SystemUser] =
    sql""" insert into system_user(is_telegram_user)
         | values (${systemUserDto.isTelegramUser})
         |""".stripMargin
      .update
      .withUniqueGeneratedKeys[SystemUser]("id", "is_telegram_user")
      .transact(transactor)

  override def find(systemUserId: SystemUserId): F[Option[SystemUser]] =
    sql""" select id, is_telegram_user from system_user
         | where id = ${systemUserId.id}
         |""".stripMargin
      .query[SystemUser]
      .option
      .transact(transactor)
}
