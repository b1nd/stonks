package ru.stonks.user.domain.repository

import ru.stonks.entity.user.{SystemUser, SystemUserId}
import ru.stonks.user.core.domain.dto.SystemUserDto

trait SystemUserRepository[F[_]] {
  def save(systemUserDto: SystemUserDto): F[SystemUser]
  def find(systemUserId: SystemUserId): F[Option[SystemUser]]
}
