package ru.stonks.bot.core.domain

import ru.stonks.bot.core.domain.usecase._

trait BotModule[F[_]] {
  def getLastUserBotCommand: GetLastUserBotCommand[F]
  def getBotUserCommandByBotCommand: GetBotUserCommandByBotCommand[F]
  def getCalculatePortfolioParamsFromBotUserInput: GetCalculatePortfolioParamsFromBotUserInput[F]
  def parseBotCommand: ParseBotCommand[F]
  def resetAllBotUserCommands: ResetAllBotUserCommands[F]
  def saveUserBotCommandWithInput: SaveUserBotCommandWithInput[F]
}
