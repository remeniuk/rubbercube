package com.bokland.rubbercube.cube.es

import org.scalatest.{BeforeAndAfterAll, ShouldMatchers, WordSpec}
import com.bokland.rubbercube.cube.{RequestResult, Cube}
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import com.bokland.rubbercube.{DateAggregationType, DateAggregation, Dimension}
import com.bokland.rubbercube.filter.Filter._
import com.bokland.rubbercube.measure.Measures.CountDistinct

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
      val cube = Cube("purchase",
        Map(Dimension("date") -> DateAggregation(DateAggregationType.Day)),
        Seq(CountDistinct(Dimension("_parent"))))

      engine.execute(cube) should be(
        RequestResult(List("date", "countdistinct-_parent"),
          List(List("2014-01-01T00:00:00.000Z", 2), List("2014-01-02T00:00:00.000Z", 1),
            List("2014-01-03T00:00:00.000Z", 1)))
      )
    }

    "be calculated with filter, applied to purchase" in {
      val cube = Cube("purchase",
        Map(Dimension("date") -> DateAggregation(DateAggregationType.Day)),
        Seq(CountDistinct(Dimension("_parent"))),
        Seq(eql(Dimension("country"), "US"), eql(Dimension("gender"), "Female"))
      )

      engine.execute(cube) should be(
        RequestResult(List("date", "countdistinct-_parent"),
          List(List("2014-01-01T00:00:00.000Z", 1), List("2014-01-02T00:00:00.000Z", 1)))
      )
    }

    "be calculated with filter, applied to parent document" in {
      val cube = Cube("purchase",
        Map(Dimension("date") -> DateAggregation(DateAggregationType.Day)),
        Seq(CountDistinct(Dimension("_parent"))),
        Seq(eql(Dimension("country"), "US"), eql(Dimension("source", cubeId = Some("user")), "Organic")),
        parentId = Some("user")
      )

      engine.execute(cube) should be(
        RequestResult(List("date", "countdistinct-_parent"),
          List(List("2014-01-01T00:00:00.000Z", 1), List("2014-01-02T00:00:00.000Z", 1)))
      )
    }
  }

}
