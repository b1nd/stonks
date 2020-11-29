package ru.stonks.nasdaq.data.dto

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class NasdaqCompanyResponse(
  symbol: String,
  name: String
)

object NasdaqCompanyResponse {
  implicit val jsonDecoder: Decoder[NasdaqCompanyResponse] = deriveDecoder
}