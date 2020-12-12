package ru.stonks.bot.domain.usecase

import cats.data.EitherT
import cats.effect.Sync
import ru.stonks.bot.core.domain.entity._
import ru.stonks.bot.core.domain.usecase._
import ru.stonks.entity.bot.BotCommand.ExcludeCompaniesCommand.ExcludeCompaniesSeparator
import ru.stonks.entity.bot.BotCommand._
import ru.stonks.entity.bot.{BotCommand, BotCommandWithInput, BotPlatform}
import ru.stonks.entity.finance.{Company, MarketIndex}
import ru.stonks.entity.user.SystemUser
import ru.stonks.finance.core.domain.usecase.GetAllNonexistentCompanies

import scala.util.Try

class ParseBotCommandImpl[F[_]](
  getLastUserBotCommand: GetLastUserBotCommand[F],
  getAllNonexistentCompanies: GetAllNonexistentCompanies[F],
  getBotUserCommandByBotCommand: GetBotUserCommandByBotCommand[F],
  resetAllBotUserCommands: ResetAllBotUserCommands[F],
  saveUserBotCommandWithInput: SaveUserBotCommandWithInput[F])(implicit
  F: Sync[F]
) extends ParseBotCommand[F] {

  import cats.implicits._

  override def run(
    command: String, systemUser: SystemUser, botPlatform: BotPlatform
  ): F[Either[BotCommandParseError, BotCommand]] = {
    val clearCmd = clearInput(command)

    if (clearCmd.startsWith(ControlCommandPrefix)) {
      parseBotControlCommand(command, systemUser, botPlatform)
    } else {
      getLastUserBotCommand.run(systemUser, botPlatform).flatMap {
        case Some(botCommand) => botCommand.nextCommand match {
          case Some(nextBotCommand) => nextBotCommand match {
            case ChooseMarketIndexCommand               => chooseMarketIndex(clearCmd, systemUser, botPlatform)
            case ChooseInvestmentAmountCommand          => chooseInvestmentAmount(clearCmd, systemUser, botPlatform)
            case ChooseMarketCapitalizationDepthCommand => chooseMarketCapitalizationDepth(clearCmd, systemUser, botPlatform)
            case ExcludeCompaniesCommand                => excludeCompaniesCommand(clearCmd, systemUser, botPlatform)
            case _ => F.pure(nextBotCommand.asRight) // bot command
          }
          case None => F.pure(botCommand.asRight) // terminal command
        }
        case None => F.pure(UnknownTextError.asLeft)
      }
    }
  }

  private def parseBotControlCommand(
    command: String, systemUser: SystemUser, botPlatform: BotPlatform
  ): F[Either[BotCommandParseError, BotCommand]] = {
    val maybeControlCommand = BotCommand.controlCommands.find(cmd => command.startsWith(cmd.stringCommand))
    maybeControlCommand match {
      case Some(controlCommand) => controlCommand match {
        case CalculatePortfolioCommand =>
          resetAllBotUserCommands.run(systemUser, botPlatform) >>
          calculatePortfolio(systemUser, botPlatform)
        case command: BotCommand => F.pure(command.asRight)
      }
      case None => F.pure(NoSuchCommandError.asLeft)
    }
  }

  private def calculatePortfolio(
    systemUser: SystemUser, botPlatform: BotPlatform
  ): F[Either[BotCommandParseError, BotCommand]] = {
    val commandWithInput = BotCommandWithInput(CalculatePortfolioCommand, None)
    saveCommand(commandWithInput, systemUser, botPlatform).value
  }

  private def chooseMarketIndex(
    command: String, systemUser: SystemUser, botPlatform: BotPlatform
  ): F[Either[BotCommandParseError, BotCommand]] =
    EitherT.fromEither[F](MarketIndex.withNameInsensitiveEither(command))
      .leftMap[BotCommandParseError](_ => UnsupportedMarketIndexError)
      .flatMap { marketIndex =>
        val commandWithInput = BotCommandWithInput(ChooseMarketIndexCommand, Some(marketIndex.toString))
        saveCommand(commandWithInput, systemUser, botPlatform)
      }.value

  private def chooseInvestmentAmount(
    command: String, systemUser: SystemUser, botPlatform: BotPlatform
  ): F[Either[BotCommandParseError, BotCommand]] =
    EitherT.fromEither[F](Try(BigDecimal(command)).toEither)
      .leftMap[BotCommandParseError](_ => InvestmentSumParseError)
      .flatMap(investmentSum => EitherT.cond[F][BotCommandParseError, BigDecimal](
        investmentSum > 0,
        investmentSum,
        InvestmentSumParseError))
      .flatMap { investmentSum =>
        val commandWithInput = BotCommandWithInput(ChooseInvestmentAmountCommand, Some(investmentSum.toString))
        saveCommand(commandWithInput, systemUser, botPlatform)
      }.value

  private def chooseMarketCapitalizationDepth(
    command: String, systemUser: SystemUser, botPlatform: BotPlatform
  ): F[Either[BotCommandParseError, BotCommand]] = (
    if (command.equalsIgnoreCase(EmptyCommand)) {
      val commandWithInput = BotCommandWithInput(ChooseMarketCapitalizationDepthCommand, None)
      saveCommand(commandWithInput, systemUser, botPlatform)
    } else {
      EitherT.fromEither[F](Try(command.toInt).toEither)
        .leftMap[BotCommandParseError](_ => InvalidMarketCapitalizationDepth)
        .flatMap(depth => EitherT.cond[F][BotCommandParseError, Int](
          depth > 0,
          depth,
          InvalidMarketCapitalizationDepth))
        .flatMap { depth =>
          val commandWithInput = BotCommandWithInput(ChooseMarketCapitalizationDepthCommand, Some(depth.toString))
          saveCommand(commandWithInput, systemUser, botPlatform)
        }
    }).value

  private def excludeCompaniesCommand(
    command: String, systemUser: SystemUser, botPlatform: BotPlatform
  ): F[Either[BotCommandParseError, BotCommand]] = (
    if (command.equalsIgnoreCase(EmptyCommand)) {
      val commandWithInput = BotCommandWithInput(ExcludeCompaniesCommand, None)
      saveCommand(commandWithInput, systemUser, botPlatform)
    } else for {
      chooseMarketIndexCommand <- EitherT.fromOptionF(
                                    getBotUserCommandByBotCommand.run(ChooseMarketIndexCommand, systemUser, botPlatform),
                                    CommandCannotBePerformedError)
      userInput                <- EitherT.fromOption[F](
                                    chooseMarketIndexCommand.input,
                                    CommandCannotBePerformedError)
      chosenMarketIndex        <- EitherT.fromEither[F](MarketIndex.withNameInsensitiveEither(userInput))
                                    .leftMap[BotCommandParseError](_ => CommandCannotBePerformedError)
      commandWithoutSpaces     = command.replaceAll("\\s+", "")
      parsedCompanies          = commandWithoutSpaces
                                   .split(ExcludeCompaniesSeparator)
                                   .map(_.toUpperCase)
                                   .map(Company).toList
      nonexistentCompanies     <- EitherT.right[BotCommandParseError](
                                  getAllNonexistentCompanies.run(parsedCompanies, chosenMarketIndex))
      companiesToExclude       <- EitherT.cond[F][BotCommandParseError, List[Company]](
                                    nonexistentCompanies.isEmpty,
                                    parsedCompanies,
                                    UnknownCompaniesError(nonexistentCompanies))
      validInput               = Some(companiesToExclude.map(_.ticker).mkString(ExcludeCompaniesSeparator))
      commandWithInput         = BotCommandWithInput(ExcludeCompaniesCommand, validInput)
      botCommand               <- saveCommand(commandWithInput, systemUser, botPlatform)
    } yield botCommand).value

  private def clearInput(input: String): String = input.trim

  private def saveCommand(
    commandWithInput: BotCommandWithInput, systemUser: SystemUser, botPlatform: BotPlatform
  ): EitherT[F, BotCommandParseError, BotCommand] = for {
    isCommandSaved <- EitherT.right(saveUserBotCommandWithInput.run(systemUser, botPlatform, commandWithInput))
    commandOrError <- EitherT.cond[F][BotCommandParseError, BotCommand](
                        isCommandSaved,
                        commandWithInput.botCommand,
                        CommandCannotBePerformedError)
  } yield commandOrError
}
