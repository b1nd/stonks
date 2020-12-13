package ru.stonks.entity.bot

object BotCommandDescriptionText {
  lazy val StartCommandDescriptionText: String = "get bot commands and their descriptions."

  lazy val ResetCommandDescriptionText: String = "cancel or reset current command."

  lazy val CalculatePortfolioCommandDescriptionText: String =
    """calculate portfolio based on chosen Market Index, top companies market capitalization
      |depth and investment amount. You can also exclude unwanted companies from your portfolio.
      |""".stripMargin.replaceAll("\\n", " ")
}
