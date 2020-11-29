package ru.stonks.nasdaq.domain.usecase

import cats.effect.Sync
import ru.stonks.entity.finance.Company
import ru.stonks.nasdaq.core.domain.usecase.GetNasdaqCompanies
import ru.stonks.nasdaq.domain.repository.PersistentNasdaqCompaniesRepository

class GetNasdaqCompaniesImpl[F[_]: Sync](
  persistentNasdaqCompaniesRepository: PersistentNasdaqCompaniesRepository[F]
) extends GetNasdaqCompanies[F] {

  override def run: F[List[Company]] = {
    persistentNasdaqCompaniesRepository.findAll
  }
}
