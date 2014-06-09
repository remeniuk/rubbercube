package com.bokland.rubbercube.sliceanddice.es

import org.scalatest.{BeforeAndAfterAll, ShouldMatchers, WordSpec}
import com.bokland.rubbercube.sliceanddice.RequestResult
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import com.bokland.rubbercube._
import com.bokland.rubbercube.filter.eql
import com.bokland.rubbercube.sliceanddice.SliceAndDice
import com.bokland.rubbercube.sliceanddice.LeftJoin
import com.bokland.rubbercube.filter.in
import com.bokland.rubbercube.measure.Sum
import com.bokland.rubbercube.DateAggregation
import com.bokland.rubbercube.Dimension
import com.bokland.rubbercube.filter.script
import com.bokland.rubbercube.measure.Div
import scala.Some
import com.bokland.rubbercube.measure.MeasureReference
import com.bokland.rubbercube.measure.CountDistinct

/**
 * Created by remeniuk on 4/29/14.
 */
class EsExecutionEngineSpec extends WordSpec with ShouldMatchers with BeforeAndAfterAll {

  var engine: EsExecutionEngine = _

  override protected def beforeAll = {
    val settings = ImmutableSettings.settingsBuilder()
      .put("cluster.name", "elasticsearch")
      .put("network.server", true).build()

    val client = new TransportClient(settings)
      .addTransportAddress(new InetSocketTransportAddress("localhost", 9300))

    engine = new EsExecutionEngine(client, "rubbercube")
  }

  "Daily unique payers count" should {
    "be calculated with no filter" in {
      val sliceAndDice = SliceAndDice("purchase",
        Seq(Dimension("event.date") -> DateAggregation(DateAggregationType.Day)),
        Seq(CountDistinct(Dimension("_parent"))))

      engine.execute(sliceAndDice) should be(
        RequestResult(List(
          Map("event.date" -> 1388534400000l, "countdistinct-_parent" -> 2),
          Map("event.date" -> 1388620800000l, "countdistinct-_parent" -> 1),
          Map("event.date" -> 1388707200000l, "countdistinct-_parent" -> 1)
        ), Some("purchase")))
    }

    "be calculated with filter, applied to purchase" in {
      val sliceAndDice = SliceAndDice("purchase",
        Seq(Dimension("event.date") -> DateAggregation(DateAggregationType.Day)),
        Seq(CountDistinct(Dimension("_parent"))),
        Left(Seq(eql(Dimension("country"), "US"), in(Dimension("gender"), SequenceValue(Seq("Female", "Male")))))
      )

      println(engine.buildRequest(sliceAndDice))

      engine.execute(sliceAndDice) should be(
        RequestResult(List(
          Map("event.date" -> 1388534400000l, "countdistinct-_parent" -> 1),
          Map("event.date" -> 1388620800000l, "countdistinct-_parent" -> 1)
        ), Some("purchase"))
      )
    }

    "be calculated with filter, applied to parent document" in {
      val sliceAndDice = SliceAndDice("purchase",
        Seq(Dimension("event.date") -> DateAggregation(DateAggregationType.Day)),
        Seq(CountDistinct(Dimension("_parent"))),
        Left(Seq(eql(Dimension("country"), "US"), eql(Dimension("source", cubeId = Some("user")), "Organic"))),
        parentId = Some("user")
      )

      engine.execute(sliceAndDice) should be(
        RequestResult(List(
          Map("event.date" -> 1388534400000l, "countdistinct-_parent" -> 1),
          Map("event.date" -> 1388620800000l, "countdistinct-_parent" -> 1)
        ), Some("purchase"))
      )
    }
  }

  //  "Revenue per day per daily cohort" in {
  //    val cube = SliceAndDice("purchase",
  //      Seq(Dimension("event.date") -> DateAggregation(DateAggregationType.Day),
  //        Dimension("registration_date") -> DateAggregation(DateAggregationType.Day)),
  //      Seq(Sum(Dimension("amount")), CountDistinct(Dimension("_parent"))))
  //
  //    engine.execute(cube) should be(
  //      RequestResult(List(
  //        Map("event.date" -> "2014-01-02T00:00:00.000Z", "registration_date" -> "2013-02-01T00:00:00.000Z", "countdistinct-_parent" -> 1, "sum-amount" -> 1.99),
  //        Map("event.date" -> "2014-01-01T00:00:00.000Z", "registration_date" -> "2013-01-01T00:00:00.000Z", "countdistinct-_parent" -> 1, "sum-amount" -> 1.99),
  //        Map("event.date" -> "2014-01-02T00:00:00.000Z", "registration_date" -> "2013-01-01T00:00:00.000Z", "countdistinct-_parent" -> 1, "sum-amount" -> 4.99),
  //        Map("event.date" -> "2014-01-03T00:00:00.000Z", "registration_date" -> "2013-02-01T00:00:00.000Z", "countdistinct-_parent" -> 1, "sum-amount" -> 99.99),
  //        Map("event.date" -> "2014-01-01T00:00:00.000Z", "registration_date" -> "2013-02-01T00:00:00.000Z", "countdistinct-_parent" -> 1, "sum-amount" -> 19.99)
  //      ), Some("purchase")))
  //  }

  "Revenue per day" in {
    val cube = SliceAndDice("purchase",
      Seq(Dimension("event.date") -> DateAggregation(DateAggregationType.Day)),
      Seq(Sum(Dimension("amount")), CountDistinct(Dimension("_parent"))))

    engine.execute(cube) should be(
      RequestResult(List(
        Map("event.date" -> 1388534400000l, "countdistinct-_parent" -> 2, "sum-amount" -> 21.979999999999997),
        Map("event.date" -> 1388620800000l, "countdistinct-_parent" -> 1, "sum-amount" -> 6.98),
        Map("event.date" -> 1388707200000l, "countdistinct-_parent" -> 1, "sum-amount" -> 99.99)
      ), Some("purchase")))
  }

  "Revenue per paying user per day" in {
    val cube = SliceAndDice("purchase",
      Seq(Dimension("event.date") -> DateAggregation(DateAggregationType.Day)),
      Seq(Div(Sum(Dimension("amount")), CountDistinct(Dimension("_parent")))))

    engine.execute(cube) should be(
      RequestResult(List(
        Map("event.date" -> 1388534400000l, "countdistinct-_parent" -> 2, "sum-amount" -> 21.979999999999997, "div-sum-amount-countdistinct-_parent" -> 10.989999999999998),
        Map("event.date" -> 1388620800000l, "countdistinct-_parent" -> 1, "sum-amount" -> 6.98, "div-sum-amount-countdistinct-_parent" -> 6.98),
        Map("event.date" -> 1388707200000l, "countdistinct-_parent" -> 1, "sum-amount" -> 99.99, "div-sum-amount-countdistinct-_parent" -> 99.99)
      ), Some("purchase")))
  }

  "Revenue by country by gender" in {
    val query = SliceAndDice("purchase",
      Seq(Dimension("country") -> CategoryAggregation,
        Dimension("gender") -> CategoryAggregation),
      Seq(Sum(Dimension("amount"), alias = Some("total_revenue"))))

    engine.execute(query) should be(
      RequestResult(List(
        Map("country" -> "us", "gender" -> "female", "total_revenue" -> 8.97),
        Map("country" -> "gb", "gender" -> "male", "total_revenue" -> 119.97999999999999)
      ), Some("purchase"))
    )
  }

  "Revenue per user per day" in {
    val revenuePerDay = SliceAndDice("purchase",
      Seq(Dimension("event.date") -> DateAggregation(DateAggregationType.Day)),
      Seq(Sum(Dimension("amount"), alias = Some("daily_revenue"))))

    val onlinePerDay = SliceAndDice("session",
      Seq(Dimension("event.date") -> DateAggregation(DateAggregationType.Day)),
      Seq(CountDistinct(Dimension("_parent"), alias = Some("dau"))))

    val result = engine.execute(LeftJoin(
      queries = Seq(revenuePerDay, onlinePerDay),
      by = Seq(Seq(Dimension("event.date", Some("purchase")), Dimension("event.date", Some("session")))),
      derivedMeasures = Seq(Div(MeasureReference("daily_revenue"), MeasureReference("dau"), Some("arppdau")))))

    result should be(
      RequestResult(List(
        Map("event.date" -> 1388534400000l, "daily_revenue" -> 21.979999999999997, "dau" -> 2, "arppdau" -> 10.989999999999998),
        Map("event.date" -> 1388620800000l, "daily_revenue" -> 6.98, "dau" -> 2, "arppdau" -> 3.49),
        Map("event.date" -> 1388707200000l, "daily_revenue" -> 99.99, "dau" -> 2, "arppdau" -> 49.995)
      )))
  }

  "Revenue from users that have payed in their registration date" in {
    val firstDepositDateFilter = script(StringValue("doc['event.date'].value/86400000 == doc['registration_date'].value/86400000"))

    val revenuePerDay = SliceAndDice("purchase",
      Seq(Dimension("event.date") -> DateAggregation(DateAggregationType.Day)),
      Seq(Sum(Dimension("amount"), alias = Some("daily_revenue"))),
      Left(Seq(firstDepositDateFilter)))

    val result = engine.execute(revenuePerDay)

    result should be(
      RequestResult(Seq(Map("event.date" -> 1388534400000l, "daily_revenue" -> 19.99)), Some("purchase"))
    )
  }

  "Total amount of purchases" in {
    val revenuePerDay = SliceAndDice("purchase",
      Nil,
      Seq(Sum(Dimension("amount"), alias = Some("daily_revenue")),
        CountDistinct(Dimension("_parent"), alias = Some("payers"))))

    val result = engine.execute(revenuePerDay)
    result should be(
      RequestResult(List(Map("payers" -> 2, "daily_revenue" -> 128.95)), Some("purchase"))
    )
  }

  "List of purchases" in {
    val purchases = SliceAndDice("purchase", size = 10, from = 0)

    val result = engine.execute(purchases)
    result.resultSet.size should be(5)
  }

  "List of fields can be excluded from resulting document[s]" in {
    val purchases = SliceAndDice("purchase", size = 10, from = 0, excludeFields = Seq("country"))

    println(engine.buildRequest(purchases))

    val result = engine.execute(purchases)
    result.resultSet.head.get("country") should be(None)
  }

  "Purchases should be aggregated by interval" in {
    val sliceAndDice = SliceAndDice("purchase",
      Seq(Dimension("amount") -> NumberAggregation(50)),
      Seq(CountDistinct(Dimension("_parent"))))

    val result = engine.execute(sliceAndDice)
    result should be(
      RequestResult(List(Map("amount" -> 0, "countdistinct-_parent" -> 2),
        Map("amount" -> 50, "countdistinct-_parent" -> 1)), Some("purchase"))
    )
  }

}
