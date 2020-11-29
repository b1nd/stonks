package ru.stonks.nasdaq.core.domain.usecase

trait RefreshPersistentNasdaqCompaniesRepository[F[_]] {
  def run: F[Boolean]
}
