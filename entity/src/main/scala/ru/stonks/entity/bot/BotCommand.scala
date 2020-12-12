package ru.stonks.entity.bot

import enumeratum._
import BotCommandUserText._
import BotCommandDescriptionText._
import BotCommand.ControlCommandPrefix

sealed trait BotCommand extends EnumEntry {
  def nextCommand: Option[BotCommand]
  def messageToUser: Option[String]
}

sealed trait ControlCommand {
  final def stringCommand: String = s"$ControlCommandPrefix$cmd"
  protected def cmd: String
  def description: String
}

sealed trait BotTerminalCommand extends BotCommand {
  final override def nextCommand: Option[BotCommand] = None
}

object BotCommand extends Enum[BotCommand] {
  final val values: IndexedSeq[BotCommand] = findValues
  final lazy val controlCommands: IndexedSeq[ControlCommand] = values
    .collect { case cmd: ControlCommand => cmd }

  final val EmptyCommand: String = "skip"
  final val ControlCommandPrefix: String = "/"

  case object StartCommand extends BotCommand with ControlCommand {
    override def nextCommand: Option[BotCommand] = None
    override def messageToUser: Option[String] = Some(StartCommandText)
    override protected def cmd: String = "start"
    override def description: String = StartCommandDescriptionText
  }

  case object ResetCommand extends BotCommand with ControlCommand {
    override def nextCommand: Option[BotCommand] = None
    override def messageToUser: Option[String] = Some(ResetCommandText)
    override protected def cmd: String = "reset"
    override def description: String = ResetCommandDescriptionText
  }

  case object CalculatePortfolioCommand extends BotCommand with ControlCommand {
    override def nextCommand: Option[BotCommand] = Some(ChooseMarketIndexCommand)
    override def messageToUser: Option[String] = None
    override protected def cmd: String = "calculate"
    override def description: String = CalculatePortfolioCommandDescriptionText
  }
  case object ChooseMarketIndexCommand extends BotCommand {
    override def nextCommand: Option[BotCommand] = Some(ChooseInvestmentAmountCommand)
    override def messageToUser: Option[String] = Some(ChooseMarketIndexCommandText)
  }
  case object ChooseInvestmentAmountCommand extends BotCommand {
    override def nextCommand: Option[BotCommand] = Some(ChooseMarketCapitalizationDepthCommand)
    override def messageToUser: Option[String] = Some(ChooseInvestmentAmountCommandText)
  }
  case object ChooseMarketCapitalizationDepthCommand extends BotCommand {
    override def nextCommand: Option[BotCommand] = Some(ExcludeCompaniesCommand)
    override def messageToUser: Option[String] = Some(ChooseMarketCapitalizationDepthCommandText)
  }
  case object ExcludeCompaniesCommand extends BotCommand {
    final val ExcludeCompaniesSeparator: String = ","
    override def nextCommand: Option[BotCommand] = Some(CalculatePortfolioTerminalCommand)
    override def messageToUser: Option[String] = Some(ExcludeCompaniesCommandText)
  }
  case object CalculatePortfolioTerminalCommand extends BotTerminalCommand {
    override def messageToUser: Option[String] = None
  }
}
