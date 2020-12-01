package ru.stonks.nasdaq.domain.usecase

import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.parallel._
import cats.{Applicative, Parallel}
import ru.stonks.nasdaq.core.domain.usecase._
import ru.stonks.nasdaq.domain.client.NasdaqCompaniesClient
import ru.stonks.nasdaq.domain.repository._

class RefreshNasdaqCompaniesRepositoryImpl[F[_] : Sync : Parallel](
  nasdaqCompaniesClient: NasdaqCompaniesClient[F],
  nasdaqCompaniesRepository: NasdaqCompaniesRepository[F]
) extends RefreshNasdaqCompaniesRepository[F] {

  override def run: F[Boolean] = for {
    actualAndCachedNasdaqCompanies <- (
      nasdaqCompaniesClient.getAll,
      nasdaqCompaniesRepository.findAll
      ).parTupled
    companiesToRemove <- Applicative[F].pure(actualAndCachedNasdaqCompanies)
      .map { case (actualNasdaqCompanies, cachedNasdaqCompanies) =>
        cachedNasdaqCompanies.diff(actualNasdaqCompanies)
      }
    isSuccessfullyDeleted <- if (companiesToRemove.isEmpty) Applicative[F].pure(true)
    else nasdaqCompaniesRepository.deleteAll(companiesToRemove)
    isUpdateSuccessful <- nasdaqCompaniesRepository.saveOrUpdateAll(actualAndCachedNasdaqCompanies._1)
  } yield isSuccessfullyDeleted && isUpdateSuccessful
}
