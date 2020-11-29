package ru.stonks.app.component

import cats.effect.{Blocker, ContextShift, Resource, Sync}
import pureconfig.module.catseffect.loadConfigF
import ru.stonks.app.config.Config

object AppConfig {
  def run[F[_] : ContextShift : Sync]: Resource[F, Config] = {
    import pureconfig.generic.auto._
    for {
      blocker <- Blocker[F]
      config <- Resource.liftF(loadConfigF[F, Config](blocker))
    } yield config
  }
}
