package ru.stonks.entity.bot

case class BotCommandWithInput(
  botCommand: BotCommand,
  userInput: Option[String]
)
