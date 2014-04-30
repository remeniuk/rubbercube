package com.bokland.rubbercube.cube.es

import org.scalatest.{BeforeAndAfterAll, ShouldMatchers, WordSpec}
import com.bokland.rubbercube.cube.{RequestResult, Cube, ExecutionEngine}
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import com.bokland.rubbercube.{DateAggregationType, DateAggregation, Dimension}
import com.bokland.rubbercube.kpi.MobileKpi
import MobileKpi.UniquePayersCount

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

  "Successfully return daily unique payers count" in {
    val cube = Cube("purchase",
      Map(Dimension("date") -> DateAggregation(DateAggregationType.Day)),
      Seq(UniquePayersCount(Dimension("_parent"))), Nil
    )

    val request = engine.buildRequest(cube)
    println("Daily paying users request: " + request)

    val result = engine.execute(cube)
    println("Result: " + result)

    result should be (
      RequestResult(List("date", "uniquepayerscount"),
        List(List("2014-01-01T00:00:00.000Z", 2), List("2014-01-02T00:00:00.000Z", 1),
          List("2014-01-03T00:00:00.000Z", 1)))
      )
  }

}
