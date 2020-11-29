package ru.stonks.finance.data.dto

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

final case class StockResponse(
  symbol: String,
  name: String,
  price: BigDecimal,
  volume: Long
)

object StockResponse {
  implicit val jsonDecoder: Decoder[StockResponse] = deriveDecoder
}
