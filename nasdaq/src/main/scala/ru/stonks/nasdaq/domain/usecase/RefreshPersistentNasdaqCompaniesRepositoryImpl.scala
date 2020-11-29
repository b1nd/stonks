package ru.stonks.nasdaq.domain.usecase

import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.parallel._
import cats.{Applicative, Parallel}
import ru.stonks.nasdaq.core.domain.usecase._
import ru.stonks.nasdaq.domain.repository._

class RefreshPersistentNasdaqCompaniesRepositoryImpl[F[_] : Sync : Parallel](
  nasdaqCompaniesRepository: NasdaqCompaniesRepository[F],
  persistentNasdaqCompaniesRepository: PersistentNasdaqCompaniesRepository[F]
) extends RefreshPersistentNasdaqCompaniesRepository[F] {

  override def run: F[Boolean] = for {
    actualAndCachedNasdaqCompanies <- (
      nasdaqCompaniesRepository.findAll,
      persistentNasdaqCompaniesRepository.findAll
      ).parTupled
    companiesToRemove <- Applicative[F].pure(actualAndCachedNasdaqCompanies)
      .map { case (actualNasdaqCompanies, cachedNasdaqCompanies) =>
        cachedNasdaqCompanies.diff(actualNasdaqCompanies)
      }
    isSuccessfullyDeleted <- if (companiesToRemove.isEmpty) Applicative[F].pure(true)
    else persistentNasdaqCompaniesRepository.deleteAll(companiesToRemove)
    isUpdateSuccessful <- persistentNasdaqCompaniesRepository.saveOrUpdateAll(actualAndCachedNasdaqCompanies._1)
  } yield isSuccessfullyDeleted && isUpdateSuccessful
}
