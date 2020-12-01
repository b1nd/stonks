package ru.stonks.nasdaq.domain.repository

import ru.stonks.entity.finance.Company

trait NasdaqCompaniesRepository[F[_]] {
  def findAll: F[List[Company]]
  def saveOrUpdateAll(companies: List[Company]): F[Boolean]
  def deleteAll(companies: List[Company]): F[Boolean]
}
