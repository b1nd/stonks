package ru.stonks.entity.finance

sealed trait MarketIndex

case object NasdaqIndex extends MarketIndex