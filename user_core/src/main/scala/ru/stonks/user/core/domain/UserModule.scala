package ru.stonks.user.core.domain

import ru.stonks.user.core.domain.usecase._

trait UserModule[F[_]] {
  def addSystemUserByTelegramUserId: AddSystemUserByTelegramUserId[F]
  def getOrAddSystemUserByTelegramUserId: GetOrAddSystemUserByTelegramUserId[F]
  def getSystemUserFromTelegramUserId: GetSystemUserFromTelegramUserId[F]
  def getTelegramUserFromSystemUserId: GetTelegramUserFromSystemUserId[F]
}
