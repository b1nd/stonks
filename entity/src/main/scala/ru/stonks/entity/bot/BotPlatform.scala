package ru.stonks.entity.bot

sealed trait BotPlatform

case object TelegramBot extends BotPlatform
