package ru.stonks.finance.data.dto

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

final case class MarketCapitalizationResponse(
  symbol: String,
  marketCap: BigDecimal
)

object MarketCapitalizationResponse {
  implicit val jsonDecoder: Decoder[MarketCapitalizationResponse] = deriveDecoder
}
