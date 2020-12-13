package ru.stonks.bot.data.di

import cats.effect.{ConcurrentEffect, ContextShift, Sync, Timer}
import com.softwaremill.macwire._
import doobie.util.transactor.Transactor
import ru.stonks.algorithm.core.domain.AlgorithmModule
import ru.stonks.bot.core.domain.BotModule
import ru.stonks.bot.core.domain.usecase._
import ru.stonks.bot.data.bot.TelegramLongPollBot
import ru.stonks.bot.data.repository.DoobieTelegramUserBotCommandRepository
import ru.stonks.bot.domain.repository.TelegramUserBotCommandRepository
import ru.stonks.bot.domain.usecase._
import ru.stonks.finance.core.domain.FinanceModule
import ru.stonks.user.core.domain.UserModule
import telegramium.bots.high.Api

class BotModuleImpl[F[_] : ContextShift : ConcurrentEffect : Sync : Timer](
  userModule: UserModule[F],
  financeModule: FinanceModule[F],
  algorithmModule: AlgorithmModule[F],
  api: Api[F],
  transactor: Transactor[F]
) extends BotModule[F] {

  import userModule._
  import financeModule._
  import algorithmModule._

  implicit private val implicitApi: Api[F] = api

  lazy val telegramUserBotCommandRepository: TelegramUserBotCommandRepository[F]
  = wire[DoobieTelegramUserBotCommandRepository[F]]

  override lazy val getLastUserBotCommand: GetLastUserBotCommand[F]
  = wire[GetLastUserBotCommandImpl[F]]

  override lazy val getBotUserCommandByBotCommand: GetBotUserCommandByBotCommand[F]
  = wire[GetBotUserCommandByBotCommandImpl[F]]

  override lazy val getCalculatePortfolioParamsFromBotUserInput: GetCalculatePortfolioParamsFromBotUserInput[F]
  = wire[GetCalculatePortfolioParamsFromBotUserInputImpl[F]]

  override lazy val parseBotCommand: ParseBotCommand[F]
  = wire[ParseBotCommandImpl[F]]

  override lazy val resetAllBotUserCommands: ResetAllBotUserCommands[F]
  = wire[ResetAllBotUserCommandsImpl[F]]

  override lazy val saveUserBotCommandWithInput: SaveUserBotCommandWithInput[F]
  = wire[SaveUserBotCommandWithInputImpl[F]]

  lazy val telegramLongPollBot: TelegramLongPollBot[F]
  = wire[TelegramLongPollBot[F]]
}
