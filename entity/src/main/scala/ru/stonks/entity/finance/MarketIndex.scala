package ru.stonks.entity.finance

import enumeratum._

sealed trait MarketIndex extends EnumEntry {
  def shortName: String
}

object MarketIndex extends Enum[MarketIndex] {
  val values: IndexedSeq[MarketIndex] = findValues

  case object NasdaqIndex extends MarketIndex {
    override def shortName: String = "Nasdaq 100"
  }
}
