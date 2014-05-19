package com.bokland.rubbercube.measure.kpi.mobile

import com.bokland.rubbercube.Aggregation
import com.bokland.rubbercube.measure.kpi.KPI
import com.bokland.rubbercube.sliceanddice.SliceAndDice
import com.bokland.rubbercube.sliceanddice.LeftJoin
import com.bokland.rubbercube.measure.Sum
import com.bokland.rubbercube.Dimension
import com.bokland.rubbercube.measure.Div
import scala.Some
import com.bokland.rubbercube.filter.{Filter, gt}
import com.bokland.rubbercube.measure.MeasureReference
import com.bokland.rubbercube.measure.Count
import com.bokland.rubbercube.measure.CountDistinct
import com.bokland.rubbercube.measure.Avg

/**
 * Created by remeniuk on 5/3/14.
 */
case object Measures {

  val _parentField = "_parent"

  val ActiveUsers = "active_users"
  val ActivePayingUsers = "active_paying_users"
  val UsersCount = "users_count"
  val ActivePayedUsers = "active_payed_users"
  val TotalRevenue = "total_revenue"
  val PurchaseCount = "purchase_count"
  val SessionsCount = "sessions_count"
  val AverageSessionLength = "average_session_length"
  val PayersPercent = "payers_percent"
  val SessionsPerUsers = "sessions_per_users"
  val PurchasesPerUsers = "purchases_per_users"
  val RevenuePerPayer = "revenue_per_payer"
  val RevenuePerUser = "revenue_per_user"
  val RevenuePerTransaction = "revenue_per_transaction"

}

case class ActiveUsers(sessionCube: String, alias: String = Measures.ActiveUsers) extends KPI {

  def generateQuery(aggregations: Seq[Aggregation], filters: Seq[Filter] = Nil): SliceAndDice =
    SliceAndDice(sessionCube, aggregations, Seq(CountDistinct(Dimension(Measures._parentField),
      alias = Some(alias))), filters = filters)

}

case class ActivePayingUsers(purchaseCube: String, alias: String = Measures.ActivePayingUsers) extends KPI {

  def generateQuery(aggregations: Seq[Aggregation], filters: Seq[Filter] = Nil): SliceAndDice =
    SliceAndDice(purchaseCube, aggregations, Seq(CountDistinct(Dimension(Measures._parentField),
      alias = Some(alias))), filters = filters)

}

case class UsersCount(userCube: String, idField: String, alias: String = Measures.UsersCount) extends KPI {

  def generateQuery(aggregations: Seq[Aggregation], filters: Seq[Filter] = Nil): SliceAndDice =
    SliceAndDice(userCube, aggregations, Seq(Count(Dimension(idField),
      alias = Some(alias))), filters = filters)

}

case class ActivePayedUsers(sessionCube: String, purchasesCountField: String,
                            alias: String = Measures.ActivePayedUsers) extends KPI {

  def generateQuery(aggregations: Seq[Aggregation], filters: Seq[Filter] = Nil): SliceAndDice =
    SliceAndDice(sessionCube, aggregations, Seq(CountDistinct(Dimension(Measures._parentField), alias = Some(alias))),
      filters :+ gt(Dimension(purchasesCountField), 0))

}

case class TotalRevenue(purchaseCube: String, purchaseAmountField: String, alias: String = Measures.TotalRevenue) extends KPI {

  def generateQuery(aggregations: Seq[Aggregation], filters: Seq[Filter] = Nil): SliceAndDice =
    SliceAndDice(purchaseCube, aggregations, Seq(Sum(Dimension(purchaseAmountField),
      alias = Some(alias))), filters = filters)

}

case class PurchaseCount(purchaseCube: String, idField: String, alias: String = Measures.PurchaseCount) extends KPI {

  def generateQuery(aggregations: Seq[Aggregation], filters: Seq[Filter] = Nil): SliceAndDice =
    SliceAndDice(purchaseCube, aggregations, Seq(Count(Dimension(idField),
      alias = Some(alias))), filters = filters)

}

case class SessionsCount(sessionCube: String, idField: String, alias: String = Measures.SessionsCount) extends KPI {

  def generateQuery(aggregations: Seq[Aggregation], filters: Seq[Filter] = Nil): SliceAndDice =
    SliceAndDice(sessionCube, aggregations, Seq(Count(Dimension(idField),
      alias = Some(alias))), filters = filters)

}

case class AverageSessionLength(sessionCube: String, sessionLengthField: String, alias: String = Measures.AverageSessionLength) extends KPI {

  def generateQuery(aggregations: Seq[Aggregation], filters: Seq[Filter] = Nil): SliceAndDice =
    SliceAndDice(sessionCube, aggregations, Seq(Avg(Dimension(sessionLengthField),
      alias = Some(alias))), filters = filters)

}

case class PayersPercent(sessionCube: String, purchaseCube: String, sessionDateField: String, purchaseDateField: String,
                         alias: String = Measures.PayersPercent) extends KPI {

  def generateQuery(aggregations: Seq[Aggregation], filters: Seq[Filter] = Nil): LeftJoin = {
    val activePayingUsers = ActivePayingUsers(purchaseCube)
    val activeUsers = ActiveUsers(sessionCube)

    LeftJoin(Seq(activePayingUsers.generateQuery(aggregations, filters), activeUsers.generateQuery(aggregations, filters)),
      Seq(Seq(Dimension(sessionDateField, Some(sessionCube)), Dimension(sessionDateField, Some(sessionCube)))),
      Seq(Div(MeasureReference(activePayingUsers.alias), MeasureReference(activeUsers.alias), Some(alias))))
  }

}

case class SessionsPerUsers(sessionCube: String, idField: String, alias: String = Measures.SessionsPerUsers) extends KPI {

  def generateQuery(aggregations: Seq[Aggregation], filters: Seq[Filter] = Nil): SliceAndDice =
    SliceAndDice(sessionCube, aggregations, Seq(Div(Count(Dimension(idField)), CountDistinct(Dimension(Measures._parentField)),
      alias = Some(alias))), filters = filters)

}

case class PurchasesPerUsers(purchaseCube: String, idField: String, alias: String = Measures.PurchasesPerUsers) extends KPI {

  def generateQuery(aggregations: Seq[Aggregation], filters: Seq[Filter] = Nil): SliceAndDice =
    SliceAndDice(purchaseCube, aggregations, Seq(Div(Count(Dimension(idField)), CountDistinct(Dimension(Measures._parentField)),
      alias = Some(alias))), filters = filters)

}

case class RevenuePerPayer(purchaseCube: String, purchaseAmountField: String, alias: String = Measures.RevenuePerPayer) extends KPI {

  def generateQuery(aggregations: Seq[Aggregation], filters: Seq[Filter] = Nil): SliceAndDice =
    SliceAndDice(purchaseCube, aggregations, Seq(Div(Sum(Dimension(purchaseAmountField)), CountDistinct(Dimension(Measures._parentField)),
      alias = Some(alias))), filters = filters)

}

case class RevenuePerUser(sessionCube: String, purchaseCube: String, sessionDateField: String, purchaseDateField: String,
                          purchaseAmountField: String, alias: String = Measures.RevenuePerUser) extends KPI {

  def generateQuery(aggregations: Seq[Aggregation], filters: Seq[Filter] = Nil): LeftJoin = {
    val totalRevenue = TotalRevenue(purchaseCube, purchaseAmountField)
    val activeUsers = ActiveUsers(sessionCube)

    LeftJoin(Seq(totalRevenue.generateQuery(aggregations, filters), activeUsers.generateQuery(aggregations, filters)),
      Seq(Seq(Dimension(sessionDateField, Some(sessionCube)), Dimension(sessionDateField, Some(sessionCube)))),
      Seq(Div(MeasureReference(totalRevenue.alias), MeasureReference(activeUsers.alias), Some(alias))))
  }

}

case class RevenuePerTransaction(purchaseCube: String, idField: String, purchaseAmountField: String, alias: String = Measures.RevenuePerTransaction) extends KPI {

  def generateQuery(aggregations: Seq[Aggregation], filters: Seq[Filter] = Nil): SliceAndDice =
    SliceAndDice(purchaseCube, aggregations, Seq(Div(Sum(Dimension(purchaseAmountField)), Count(Dimension(idField)),
      alias = Some(alias))), filters = filters)

}

