package ru.stonks.nasdaq.domain.usecase

import cats.effect.Sync
import ru.stonks.entity.finance.Company
import ru.stonks.nasdaq.core.domain.usecase.GetNasdaqCompanies
import ru.stonks.nasdaq.domain.repository.NasdaqCompaniesRepository

class GetNasdaqCompaniesImpl[F[_] : Sync](
  nasdaqCompaniesRepository: NasdaqCompaniesRepository[F]
) extends GetNasdaqCompanies[F] {

  override def run: F[List[Company]] = {
    nasdaqCompaniesRepository.findAll
  }
}
