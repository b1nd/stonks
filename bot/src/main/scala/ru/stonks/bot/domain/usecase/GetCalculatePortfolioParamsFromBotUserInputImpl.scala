package ru.stonks.bot.domain.usecase

import cats.data.OptionT
import cats.effect.Sync
import ru.stonks.bot.core.domain.usecase.GetCalculatePortfolioParamsFromBotUserInput
import ru.stonks.bot.domain.repository.TelegramUserBotCommandRepository
import ru.stonks.entity.algorithm.CalculatePortfolioParams
import ru.stonks.entity.bot.BotCommand.ExcludeCompaniesCommand.ExcludeCompaniesSeparator
import ru.stonks.entity.bot.BotCommand._
import ru.stonks.entity.bot._
import ru.stonks.entity.finance.{Company, MarketIndex}
import ru.stonks.entity.user.SystemUser
import ru.stonks.user.core.domain.usecase.GetTelegramUserFromSystemUserId

import scala.util.Try

class GetCalculatePortfolioParamsFromBotUserInputImpl[F[_] : Sync](
  getTelegramUserFromSystemUserId: GetTelegramUserFromSystemUserId[F],
  telegramUserBotCommandRepository: TelegramUserBotCommandRepository[F]
) extends GetCalculatePortfolioParamsFromBotUserInput[F] {

  override def run(systemUser: SystemUser, botPlatform: BotPlatform): F[Option[CalculatePortfolioParams]] = {
    val maybeUserCommands = botPlatform match {
      case TelegramBot => for {
        telegramUser <- OptionT(getTelegramUserFromSystemUserId.run(systemUser.id))
        userCommands <- OptionT.liftF(telegramUserBotCommandRepository.findAllByUser(telegramUser.id))
      } yield userCommands
    }
    for {
      userCommands       <- maybeUserCommands
      commandToUserInput = userCommands.groupBy(_.command)
      marketIndex        <- parseMarketIndex(commandToUserInput)
      investmentSum      <- parseInvestmentSum(commandToUserInput)
      marketCapDepth     <- parseMarketCapDepth(commandToUserInput)
      excludeCompanies   <- parseExcludedCompanies(commandToUserInput)
      params             = CalculatePortfolioParams(marketIndex, investmentSum, marketCapDepth, excludeCompanies)
    } yield params
  }.value

  private def parseMarketIndex(
    commandToUserInput: Map[BotCommand, List[BotUserCommand]]
  ): OptionT[F, MarketIndex] = OptionT.fromOption[F](for {
    chooseMarketIndexCommands     <- commandToUserInput.get(ChooseMarketIndexCommand)
    chooseMarketIndexCommand      <- chooseMarketIndexCommands.headOption
    chooseMarketIndexCommandInput <- chooseMarketIndexCommand.input
    marketIndex                   <- MarketIndex.withNameInsensitiveOption(chooseMarketIndexCommandInput)
  } yield marketIndex)

  private def parseInvestmentSum(
    commandToUserInput: Map[BotCommand, List[BotUserCommand]]
  ): OptionT[F, BigDecimal] = OptionT.fromOption[F](for {
    investmentAmountCommands     <- commandToUserInput.get(ChooseInvestmentAmountCommand)
    investmentAmountCommand      <- investmentAmountCommands.headOption
    investmentAmountCommandInput <- investmentAmountCommand.input
    investmentSum                <- Try(BigDecimal(investmentAmountCommandInput)).toOption
  } yield investmentSum)

  private def parseMarketCapDepth(
    commandToUserInput: Map[BotCommand, List[BotUserCommand]]
  ): OptionT[F, Option[Int]] = for {
    marketCapDepthCommand <- OptionT.fromOption[F](for {
      marketCapDepthCommands <- commandToUserInput.get(ChooseMarketCapitalizationDepthCommand)
      marketCapDepthCommand  <- marketCapDepthCommands.headOption
    } yield marketCapDepthCommand)
    marketCapDepth = for {
      marketCapDepthInput <- marketCapDepthCommand.input
      marketCapDepth      <- Try(marketCapDepthInput.toInt).toOption
    } yield marketCapDepth
  } yield marketCapDepth

  private def parseExcludedCompanies(
    commandToUserInput: Map[BotCommand, List[BotUserCommand]]
  ): OptionT[F, List[Company]] = for {
    excludeCompaniesCommand <- OptionT.fromOption[F](for {
      excludeCompaniesCommands <- commandToUserInput.get(ExcludeCompaniesCommand)
      excludeCompaniesCommand  <- excludeCompaniesCommands.headOption
    } yield excludeCompaniesCommand)
    excludedCompanies = (for {
      excludeCompaniesInput <- excludeCompaniesCommand.input
      excludeCompanies      = excludeCompaniesInput
                                .split(ExcludeCompaniesSeparator)
                                .map(Company).toList
    } yield excludeCompanies).getOrElse(Nil)
  } yield excludedCompanies
}
