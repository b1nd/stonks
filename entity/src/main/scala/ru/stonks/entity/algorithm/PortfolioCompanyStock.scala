package ru.stonks.entity.algorithm

import ru.stonks.entity.finance.Company

case class PortfolioCompanyStock(
  company: Company,
  count: Long,
  sum: BigDecimal
)
