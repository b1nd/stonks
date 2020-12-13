package ru.stonks.bot.core.domain.entity

import ru.stonks.entity.finance.Company

sealed trait BotCommandParseError {
  def message: String
}

case object NoSuchCommandError extends BotCommandParseError {
  override def message: String = "Unknown command"
}
case object UnknownTextError extends BotCommandParseError {
  override def message: String = "Unknown text"
}
case object UnsupportedMarketIndexError extends BotCommandParseError {
  override def message: String = "This Market Index is wrong or not supported!"
}
case object InvestmentSumParseError extends BotCommandParseError {
  override def message: String = "Investment sum must be a number greater than zero!"
}
case object InvalidMarketCapitalizationDepth extends BotCommandParseError {
  override def message: String = "Market capitalization depth must be a nonzero positive integer number!"
}
case class UnknownCompaniesError(companies: List[Company]) extends BotCommandParseError {
  override def message: String = s"Unknown companies: ${companies.map(_.ticker).mkString(", ")}"
}
case object CommandCannotBePerformedError extends BotCommandParseError {
  override def message: String = "Some internal error happened, command cannot be performed!"
}
