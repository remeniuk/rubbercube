package com.bokland.rubbercube.sliceanddice.es

import org.scalatest.{BeforeAndAfterAll, ShouldMatchers, WordSpec}
import com.bokland.rubbercube.sliceanddice.{LeftJoin, RequestResult, SliceAndDice}
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import com.bokland.rubbercube.{CategoryAggregation, DateAggregationType, DateAggregation, Dimension}
import com.bokland.rubbercube.measure._
import com.bokland.rubbercube.filter._
import com.bokland.rubbercube.measure.MeasureReference

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
        Seq(Dimension("date") -> DateAggregation(DateAggregationType.Day)),
        Seq(CountDistinct(Dimension("_parent"))))

      engine.execute(sliceAndDice) should be(
        RequestResult(List(
          Map("date" -> "2014-01-01T00:00:00.000Z", "countdistinct-_parent" -> 2),
          Map("date" -> "2014-01-02T00:00:00.000Z", "countdistinct-_parent" -> 1),
          Map("date" -> "2014-01-03T00:00:00.000Z", "countdistinct-_parent" -> 1)
        ), Some("purchase")))
    }

    "be calculated with filter, applied to purchase" in {
      val sliceAndDice = SliceAndDice("purchase",
        Seq(Dimension("date") -> DateAggregation(DateAggregationType.Day)),
        Seq(CountDistinct(Dimension("_parent"))),
        Seq(eql(Dimension("country"), "US"), eql(Dimension("gender"), "Female"))
      )

      engine.execute(sliceAndDice) should be(
        RequestResult(List(
          Map("date" -> "2014-01-01T00:00:00.000Z", "countdistinct-_parent" -> 1),
          Map("date" -> "2014-01-02T00:00:00.000Z", "countdistinct-_parent" -> 1)
        ), Some("purchase"))
      )
    }

    "be calculated with filter, applied to parent document" in {
      val sliceAndDice = SliceAndDice("purchase",
        Seq(Dimension("date") -> DateAggregation(DateAggregationType.Day)),
        Seq(CountDistinct(Dimension("_parent"))),
        Seq(eql(Dimension("country"), "US"), eql(Dimension("source", cubeId = Some("user")), "Organic")),
        parentId = Some("user")
      )

      engine.execute(sliceAndDice) should be(
        RequestResult(List(
          Map("date" -> "2014-01-01T00:00:00.000Z", "countdistinct-_parent" -> 1),
          Map("date" -> "2014-01-02T00:00:00.000Z", "countdistinct-_parent" -> 1)
        ), Some("purchase"))
      )
    }
  }

  "Revenue per day per daily cohort" in {
    val cube = SliceAndDice("purchase",
      Seq(Dimension("date") -> DateAggregation(DateAggregationType.Day),
        Dimension("registration_date") -> DateAggregation(DateAggregationType.Day)),
      Seq(Sum(Dimension("amount")), CountDistinct(Dimension("_parent"))))

    engine.execute(cube) should be(
      RequestResult(List(
        Map("date" -> "2014-01-02T00:00:00.000Z", "registration_date" -> "2013-02-01T00:00:00.000Z", "countdistinct-_parent" -> 1, "sum-amount" -> 1.99),
        Map("date" -> "2014-01-01T00:00:00.000Z", "registration_date" -> "2013-01-01T00:00:00.000Z", "countdistinct-_parent" -> 1, "sum-amount" -> 1.99),
        Map("date" -> "2014-01-02T00:00:00.000Z", "registration_date" -> "2013-01-01T00:00:00.000Z", "countdistinct-_parent" -> 1, "sum-amount" -> 4.99),
        Map("date" -> "2014-01-03T00:00:00.000Z", "registration_date" -> "2013-02-01T00:00:00.000Z", "countdistinct-_parent" -> 1, "sum-amount" -> 99.99),
        Map("date" -> "2014-01-01T00:00:00.000Z", "registration_date" -> "2013-02-01T00:00:00.000Z", "countdistinct-_parent" -> 1, "sum-amount" -> 19.99)
      ), Some("purchase")))
  }

  "Revenue per day" in {
    val cube = SliceAndDice("purchase",
      Seq(Dimension("date") -> DateAggregation(DateAggregationType.Day)),
      Seq(Sum(Dimension("amount")), CountDistinct(Dimension("_parent"))))

    engine.execute(cube) should be(
      RequestResult(List(
        Map("date" -> "2014-01-01T00:00:00.000Z", "countdistinct-_parent" -> 2, "sum-amount" -> 21.979999999999997),
        Map("date" -> "2014-01-02T00:00:00.000Z", "countdistinct-_parent" -> 1, "sum-amount" -> 6.98),
        Map("date" -> "2014-01-03T00:00:00.000Z", "countdistinct-_parent" -> 1, "sum-amount" -> 99.99)
      ), Some("purchase")))
  }

  "Revenue per paying user per day" in {
    val cube = SliceAndDice("purchase",
      Seq(Dimension("date") -> DateAggregation(DateAggregationType.Day)),
      Seq(Div(Sum(Dimension("amount")), CountDistinct(Dimension("_parent")))))

    engine.execute(cube) should be(
      RequestResult(List(
        Map("date" -> "2014-01-01T00:00:00.000Z", "countdistinct-_parent" -> 2, "sum-amount" -> 21.979999999999997, "div-sum-amount-countdistinct-_parent" -> 10.989999999999998),
        Map("date" -> "2014-01-02T00:00:00.000Z", "countdistinct-_parent" -> 1, "sum-amount" -> 6.98, "div-sum-amount-countdistinct-_parent" -> 6.98),
        Map("date" -> "2014-01-03T00:00:00.000Z", "countdistinct-_parent" -> 1, "sum-amount" -> 99.99, "div-sum-amount-countdistinct-_parent" -> 99.99)
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
      Seq(Dimension("date") -> DateAggregation(DateAggregationType.Day)),
      Seq(Sum(Dimension("amount"), alias = Some("daily_revenue"))))

    val onlinePerDay = SliceAndDice("session",
      Seq(Dimension("date") -> DateAggregation(DateAggregationType.Day)),
      Seq(CountDistinct(Dimension("_parent"), alias = Some("dau"))))

    val result = engine.execute(LeftJoin(
      queries = Seq(revenuePerDay, onlinePerDay),
      by = Seq(Seq(Dimension("date", Some("purchase")), Dimension("date", Some("session")))),
      derivedMeasures = Seq(Div(MeasureReference("daily_revenue"), MeasureReference("dau"), Some("arppdau")))))

    result should be(
      RequestResult(List(
        Map("date" -> "2014-01-01T00:00:00.000Z", "dau" -> 2, "daily_revenue" -> 21.979999999999997, "arppdau" -> 10.989999999999998),
        Map("date" -> "2014-01-02T00:00:00.000Z", "dau" -> 2, "daily_revenue" -> 6.98, "arppdau" -> 3.49),
        Map("date" -> "2014-01-03T00:00:00.000Z", "dau" -> 2, "daily_revenue" -> 99.99, "arppdau" -> 49.995)
      )))
  }

}
