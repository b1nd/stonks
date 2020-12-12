package ru.stonks.user.data.repository

import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor.Transactor
import ru.stonks.entity.user.{SystemUserId, TelegramUser, TelegramUserId}
import ru.stonks.user.core.domain.dto.TelegramUserDto
import ru.stonks.user.domain.repository.TelegramUserRepository

class DoobieTelegramUserRepository[F[_] : Sync](
  transactor: Transactor[F]
) extends TelegramUserRepository[F] {

  override def save(telegramUserDto: TelegramUserDto): F[TelegramUser] =
    sql""" insert into telegram_user(id, system_user_id)
         | values (${telegramUserDto.telegramUserId.id}, ${telegramUserDto.systemUserId.id})
         |""".stripMargin
      .update
      .withUniqueGeneratedKeys[TelegramUser]("id", "system_user_id")
      .transact(transactor)

  override def find(telegramUserId: TelegramUserId): F[Option[TelegramUser]] =
    sql""" select id, system_user_id from telegram_user
         | where id = ${telegramUserId.id}
         |""".stripMargin
      .query[TelegramUser]
      .option
      .transact(transactor)

  override def findBySystemUserId(systemUserId: SystemUserId): F[Option[TelegramUser]] =
    sql""" select id, system_user_id from telegram_user
         | where system_user_id = ${systemUserId.id}
         |""".stripMargin
      .query[TelegramUser]
      .option
      .transact(transactor)
}
