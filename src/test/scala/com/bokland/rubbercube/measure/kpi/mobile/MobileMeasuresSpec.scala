package com.bokland.rubbercube.measure.kpi.mobile

import org.scalatest._
import com.bokland.rubbercube.sliceanddice.es.EsExecutionEngine
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import com.bokland.rubbercube.{DateAggregationType, DateAggregation, Dimension}
import com.bokland.rubbercube.sliceanddice.RequestResult

/**
 * Created by remeniuk on 5/3/14.
 */
class MobileMeasuresSpec extends WordSpec with ShouldMatchers with BeforeAndAfterAll {

  var engine: EsExecutionEngine = _

  override protected def beforeAll = {
    val settings = ImmutableSettings.settingsBuilder()
      .put("cluster.name", "elasticsearch")
      .put("network.server", true).build()

    val client = new TransportClient(settings)
      .addTransportAddress(new InetSocketTransportAddress("localhost", 9300))

    engine = new EsExecutionEngine(client, "rubbercube")
  }

  "Calcualte ARPDAU" in {
    val query = RevenuePerUser(
      sessionCube = "session",
      purchaseCube = "purchase",
      sessionDateField = "date",
      purchaseDateField = "date",
      purchaseAmountField = "amount")
      .generateQuery(Seq(Dimension("date") -> DateAggregation(DateAggregationType.Day)))

    val result = engine.execute(query)

    result should be(RequestResult(
      Seq(
        Map("date" -> "2014-01-01T00:00:00.000Z", "total_revenue" -> 21.979999999999997, "active_users" -> 2, "revenue_per_user" -> 10.989999999999998),
        Map("date" -> "2014-01-01T00:00:00.000Z", "total_revenue" -> 6.98, "active_users" -> 2, "revenue_per_user" -> 3.49),
        Map("date" -> "2014-01-01T00:00:00.000Z", "total_revenue" -> 99.99, "active_users" -> 2, "revenue_per_user" -> 49.995)),
      None)
    )
  }

}
