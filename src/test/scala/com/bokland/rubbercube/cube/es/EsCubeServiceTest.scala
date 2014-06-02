package com.bokland.rubbercube.cube.es

import org.scalatest._
import com.bokland.rubbercube.sliceanddice.es.{EsRequest, EsExecutionEngine}
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import com.bokland.rubbercube.{NumberRangeDefaults, CategoriesDefaults, Dimension}
import com.bokland.rubbercube.cube.CubeService
import org.elasticsearch.action.search.SearchRequestBuilder
import com.bokland.rubbercube.sliceanddice.ExecutionEngine

/**
 * Created by remeniuk on 5/11/14.
 */
class EsCubeServiceTest extends WordSpec with ShouldMatchers with BeforeAndAfterAll {

  var service: CubeService[EsRequest] = _

  override protected def beforeAll = {
    val settings = ImmutableSettings.settingsBuilder()
      .put("cluster.name", "elasticsearch")
      .put("network.server", true).build()

    val client = new TransportClient(settings)
      .addTransportAddress(new InetSocketTransportAddress("localhost", 9300))

    service = new CubeService[EsRequest] {
      val executionEngine: ExecutionEngine[EsRequest] =
        new EsExecutionEngine(client, "rubbercube")
    }
  }

  "Dimension defaults" should {

    "be loaded for date dimension" in {
      val dimensionWithValues = service.withDefaults("purchase",
        Dimension("registration_date", valueType = Some("date")))

      dimensionWithValues.defaults should not be (None)
    }

    "be loaded for number dimension" in {
      val dimensionWithValues = service.withDefaults("purchase",
        Dimension("amount", valueType = Some("number")))
      dimensionWithValues.defaults should be (Some(NumberRangeDefaults(1.99, 99.99)))
    }

    "be loaded for category dimension" in {
      val dimensionWithValues = service.withDefaults("purchase",
        Dimension("country", valueType = Some("category")))
      dimensionWithValues.defaults should be (Some(CategoriesDefaults(Seq("us", "gb"))))
    }
  }

}
