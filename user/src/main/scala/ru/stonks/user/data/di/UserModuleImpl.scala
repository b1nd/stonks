package ru.stonks.user.data.di

import cats.effect.Sync
import com.softwaremill.macwire._
import doobie.util.transactor.Transactor
import ru.stonks.user.core.domain.UserModule
import ru.stonks.user.core.domain.usecase._
import ru.stonks.user.data.repository._
import ru.stonks.user.domain.repository._
import ru.stonks.user.domain.usecase._

class UserModuleImpl[F[_] : Sync](
  transactor: Transactor[F]
) extends UserModule[F] {

  lazy val systemUserRepository: SystemUserRepository[F]
  = wire[DoobieSystemUserRepository[F]]

  lazy val telegramUserRepository: TelegramUserRepository[F]
  = wire[DoobieTelegramUserRepository[F]]

  override lazy val addSystemUserByTelegramUserId: AddSystemUserByTelegramUserId[F]
  = wire[AddSystemUserByTelegramUserIdImpl[F]]

  override lazy val getOrAddSystemUserByTelegramUserId: GetOrAddSystemUserByTelegramUserId[F]
  = wire[GetOrAddSystemUserByTelegramUserIdImpl[F]]

  override lazy val getSystemUserFromTelegramUserId: GetSystemUserFromTelegramUserId[F]
  = wire[GetSystemUserFromTelegramUserIdImpl[F]]

  override lazy val getTelegramUserFromSystemUserId: GetTelegramUserFromSystemUserId[F]
  = wire[GetTelegramUserFromSystemUserIdImpl[F]]
}
