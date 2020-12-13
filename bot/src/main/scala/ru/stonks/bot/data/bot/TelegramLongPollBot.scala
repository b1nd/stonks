package ru.stonks.bot.data.bot

import cats.effect.{Concurrent, ConcurrentEffect, Sync, Timer}
import cats.syntax.applicativeError._
import cats.syntax.flatMap._
import cats.syntax.functor._
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import ru.stonks.algorithm.core.domain.usecase.CalculatePortfolio
import ru.stonks.bot.core.domain.usecase._
import ru.stonks.bot.data.syntax.BotPrettyPrint._
import ru.stonks.entity.bot.BotCommand._
import ru.stonks.entity.bot.{BotCommand, BotUserCommand, TelegramBot}
import ru.stonks.entity.finance.MarketIndex
import ru.stonks.entity.user.{SystemUser, TelegramUserId}
import ru.stonks.user.core.domain.usecase.GetOrAddSystemUserByTelegramUserId
import telegramium.bots.high.implicits._
import telegramium.bots.high.{Api, InlineKeyboardButton, InlineKeyboardMarkup, LongPollBot}
import telegramium.bots.{CallbackQuery, Chat, ChatIntId, Message}

class TelegramLongPollBot[F[_]](
  parseBotCommand: ParseBotCommand[F],
  resetAllBotUserCommands: ResetAllBotUserCommands[F],
  getCalculatePortfolioParamsFromBotUserInput: GetCalculatePortfolioParamsFromBotUserInput[F],
  calculatePortfolio: CalculatePortfolio[F],
  getBotUserCommandByBotCommand: GetBotUserCommandByBotCommand[F],
  getOrAddSystemUserByTelegramUserId: GetOrAddSystemUserByTelegramUserId[F])(implicit
  api: Api[F],
  F: Sync[F],
  timer: Timer[F],
  ce: ConcurrentEffect[F]
) extends LongPollBot[F](api) {

  implicit def botLogger: Logger[F] = Slf4jLogger.getLogger[F]

  override def onMessage(msg: Message): F[Unit] =
    msg.text.flatMap { cmd =>
      msg.from.map { user =>
        Concurrent[F].start(onBotCommand(cmd, user.id, msg.chat)).void
      }
    }.getOrElse(F.unit)

  override def onCallbackQuery(query: CallbackQuery): F[Unit] =
    query.data.flatMap { cmd =>
      query.message.map { msg =>
        Concurrent[F].start {
          editMessageReplyMarkup(
            chatId = Some(ChatIntId(msg.chat.id)),
            messageId = Some(msg.messageId),
            replyMarkup = None
          ).exec >>
          onBotCommand(cmd, query.from.id, msg.chat)
        }.void
      }
    }.getOrElse(F.unit)

  private def onBotCommand(cmd: String, telegramUserId: Int, chat: Chat): F[Unit] = {
    for {
      systemUser        <- getOrAddSystemUserByTelegramUserId.run(TelegramUserId(telegramUserId))
      botCommandOrError <- parseBotCommand.run(cmd, systemUser, TelegramBot)
      _                 <- botCommandOrError match {
        case Left(error)       => sendMessage(ChatIntId(chat.id), error.message).exec.void
        case Right(botCommand) => botCommand.nextCommand match {
          case Some(nextCommand) => doBotCommand(chat, systemUser, nextCommand)
          case None              => doBotCommand(chat, systemUser, botCommand)
        }
      }
    } yield ()
  }.onError { err =>
    Logger[F].error(err)(s"$cmd ($telegramUserId): ${err.getMessage}") >>
    sendMessage(ChatIntId(chat.id), InternalError).exec.void
  }

  private def doBotCommand(chat: Chat, systemUser: SystemUser, botCommand: BotCommand): F[Unit] = botCommand match {
    case ResetCommand =>
      resetAllBotUserCommands.run(systemUser, TelegramBot) >>
      sendBotCommandUserMessage(chat, botCommand)
    case CalculatePortfolioTerminalCommand =>
      getCalculatePortfolioParamsFromBotUserInput.run(systemUser, TelegramBot).flatMap {
        case Some(params) => for {
          portfolioOrError <- calculatePortfolio.run(params)
          _                <- sendMessage(ChatIntId(chat.id), portfolioOrError match {
            case Left(error)      => error.message
            case Right(portfolio) => portfolio.prettyPrint
          }).exec
        } yield ()
        case None => sendMessage(ChatIntId(chat.id), InternalError).exec.void
      } >> resetAllBotUserCommands.run(systemUser, TelegramBot).void
    case ChooseMarketIndexCommand =>
      sendMessage(
        chatId = ChatIntId(chat.id),
        text = botCommand.messageToUser.getOrElse(""),
        replyMarkup = Some(InlineKeyboardMarkup.singleColumn(
          MarketIndex.values.map { marketIndex =>
            InlineKeyboardButton.callbackData(marketIndex.shortName, marketIndex.toString)
          }.toList
        ))
      ).exec.void
    case ChooseInvestmentAmountCommand =>
      getBotUserCommandByBotCommand.run(ChooseMarketIndexCommand, systemUser, TelegramBot).flatMap {
        case Some(BotUserCommand(_, _, ChooseMarketIndexCommand, Some(input))) =>
          MarketIndex.withNameInsensitiveOption(input).map { marketIndex =>
            sendMessage(ChatIntId(chat.id), s"You have chosen ${marketIndex.shortName}").exec.void
          }.getOrElse(F.unit)
        case _ => F.unit
      } >> sendBotCommandUserMessage(chat, botCommand)
    case _ => sendBotCommandUserMessage(chat, botCommand)
  }

  private def sendBotCommandUserMessage(chat: Chat, botCommand: BotCommand): F[Unit] = botCommand.messageToUser
    .map { messageText =>
      sendMessage(ChatIntId(chat.id), messageText).exec.void
    }.getOrElse(F.unit)

  private lazy val InternalError = "Some internal error occurred, " +
    s"consider ${ResetCommand.stringCommand} or try again later"
}
