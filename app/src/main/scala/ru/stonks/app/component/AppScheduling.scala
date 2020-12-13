package ru.stonks.app.component

import java.util.concurrent.TimeUnit

import cats.effect.{Concurrent, ConcurrentEffect, ContextShift, Resource, Sync, Timer}
import cats.syntax.applicativeError._
import cats.syntax.flatMap._
import cats.syntax.functor._
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import ru.stonks.app.config.SchedulingConfig
import ru.stonks.entity.finance.MarketIndex

import scala.concurrent.duration.FiniteDuration

class AppScheduling[F[_] : Sync : ConcurrentEffect](
  schedulingConfig: SchedulingConfig,
  modules: AppModules[F])(implicit
  cs: ContextShift[F],
  F: Timer[F]
) {
  implicit def schedulingLogger: Logger[F] = Slf4jLogger.getLogger[F]

  def refreshFinanceCache: F[Unit] = for {
    _ <- F.sleep(FiniteDuration(schedulingConfig.financeRefreshSecondsRate, TimeUnit.SECONDS))
    updatedIndexes <- modules.financeModule.refreshAllFinanceRepositories.run(MarketIndex.values.toList)
      .onError { err =>
        Logger[F].error(err)(err.getMessage) >> cs.shift >> refreshFinanceCache
      }
    _ <- Logger[F].info("Updated Market Indexes: " + updatedIndexes.mkString(", "))
    next <- cs.shift >> refreshFinanceCache
  } yield next

  def run: Resource[F, Unit] = Resource.liftF {
    Concurrent[F].start {
      refreshFinanceCache
    }.void
  }
}

object AppScheduling {
  def apply[F[_]](
    schedulingConfig: SchedulingConfig,
    modules: AppModules[F])(implicit
    sync: Sync[F],
    ce: ConcurrentEffect[F],
    cs: ContextShift[F],
    timer: Timer[F]
  ): AppScheduling[F] = new AppScheduling(schedulingConfig, modules)
}
