package ru.stonks.entity.bot

object BotCommandUserText {
  private lazy val OptionalCommand: String = s"(optional, type '${BotCommand.EmptyCommand}' to skip)"

  private lazy val ControlCommandsWithDescription = BotCommand.controlCommands.map { controlCommand =>
    s"${controlCommand.stringCommand} - ${controlCommand.description}"
  }.mkString("\n")

  lazy val StartCommandText: String =
    s"""This is Investment Portfolio calculation bot based on Major World Market Indices weighted by companies market capitalization.
       |It is useful if you want to invest a large amount for a long time.
       |The value of your portfolio changes with the chosen market index. Bot commands:
       |$ControlCommandsWithDescription
       |""".stripMargin

  lazy val ResetCommandText = "Successfully reset"

  lazy val ChooseMarketIndexCommandText = "Choose market index"

  lazy val ChooseInvestmentAmountCommandText = "Input sum to invest ($)"

  lazy val ChooseMarketCapitalizationDepthCommandText = s"Input top market capitalization depth $OptionalCommand"

  lazy val ExcludeCompaniesCommandText = s"Companies tickers to exclude separated by comma $OptionalCommand"
}
