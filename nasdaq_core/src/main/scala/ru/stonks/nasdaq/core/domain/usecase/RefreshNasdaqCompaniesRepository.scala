package ru.stonks.nasdaq.core.domain.usecase

trait RefreshNasdaqCompaniesRepository[F[_]] {
  def run: F[Boolean]
}
