package ru.stonks.finance.domain.usecase

import ru.stonks.entity.finance.{Company, MarketCapitalization}
import ru.stonks.finance.core.domain.usecase.GetCompanyMarketCapitalization
import ru.stonks.finance.domain.repository.MarketCapitalizationRepository

class GetCompanyMarketCapitalizationImpl[F[_]](
  marketCapitalizationRepository: MarketCapitalizationRepository[F]
) extends GetCompanyMarketCapitalization[F] {

  override def run(company: Company): F[Option[MarketCapitalization]] = {
    marketCapitalizationRepository.find(company)
  }
}
