package ru.stonks.bot.data.repository

import cats.effect.Sync
import cats.syntax.functor._
import doobie.implicits._
import doobie.util.Read
import doobie.util.transactor.Transactor
import ru.stonks.bot.domain.repository.TelegramUserBotCommandRepository
import ru.stonks.entity.bot._
import ru.stonks.entity.user.TelegramUserId

class DoobieTelegramUserBotCommandRepository[F[_] : Sync](
  transactor: Transactor[F]
) extends TelegramUserBotCommandRepository[F] {

  implicit val botUserCommandRead: Read[BotUserCommand] =
    Read[(Long, TelegramUserId, String, Option[String])].map { case (id, telegramUserId, botCommand, input) =>
      BotUserCommand(id, telegramUserId, BotCommand.withNameInsensitive(botCommand), input)
    }

  override def findLastUserBotCommand(telegramUserId: TelegramUserId): F[Option[BotCommand]] =
    sql""" select command from telegram_bot_user_command
         | where telegram_user_id = ${telegramUserId.id}
         | order by created_at desc
         | limit 1
         |""".stripMargin
      .query[String]
      .option
      .transact(transactor)
      .map(_.flatMap(BotCommand.withNameInsensitiveOption))

  override def findByBotCommand(telegramUserId: TelegramUserId, botCommand: BotCommand): F[Option[BotUserCommand]] =
    sql""" select id, telegram_user_id, command, input from telegram_bot_user_command
         | where telegram_user_id = ${telegramUserId.id}
         | and command = ${botCommand.toString}
         |""".stripMargin
      .query[BotUserCommand]
      .option
      .transact(transactor)

  override def findAllByUser(telegramUserId: TelegramUserId): F[List[BotUserCommand]] =
    sql""" select id, telegram_user_id, command, input from telegram_bot_user_command
         | where telegram_user_id = ${telegramUserId.id}
         |""".stripMargin
      .query[BotUserCommand]
      .to[List]
      .transact(transactor)

  override def saveUserBotCommandWithInput(
    telegramUserId: TelegramUserId,
    botCommandWithInput: BotCommandWithInput
  ): F[BotUserCommand] =
    sql""" insert into telegram_bot_user_command(telegram_user_id, command, input)
         | values (${telegramUserId.id}, ${botCommandWithInput.botCommand.toString}, ${botCommandWithInput.userInput})
         |""".stripMargin
      .update
      .withUniqueGeneratedKeys[BotUserCommand]("id", "telegram_user_id", "command", "input")
      .transact(transactor)

  override def deleteAllByUser(telegramUserId: TelegramUserId): F[Boolean] =
    sql""" delete from telegram_bot_user_command
         | where telegram_user_id = ${telegramUserId.id}
         |""".stripMargin
      .update
      .run
      .map(_ => true)
      .transact(transactor)
}
