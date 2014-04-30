package com.bokland.rubbercube.cube.es

import org.elasticsearch.action.search.SearchRequestBuilder
import com.bokland.rubbercube.cube.{RequestResult, Cube, ExecutionEngine}
import org.elasticsearch.client.transport.TransportClient
import com.bokland.rubbercube.marshaller.es.EsFilterMarshaller
import com.bokland.rubbercube.kpi.es.EsAggregationQueryBuilder
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram
import scala.collection.JavaConversions._
import org.elasticsearch.search.aggregations.Aggregation
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality

/**
 * Created by remeniuk on 4/29/14.
 */
class EsExecutionEngine(client: TransportClient, index: String) extends ExecutionEngine[SearchRequestBuilder] {

  def buildRequest(cube: Cube): SearchRequestBuilder = {
    import cube._

    val search = client.prepareSearch(index).setTypes(cube.id).setSize(0)

    kpis.foreach {
      kpi => search.addAggregation(EsAggregationQueryBuilder.buildAggregationQuery(kpi, aggregations))
    }

    filters.foreach {
      filter => search.setQuery(EsFilterMarshaller.marshal(filter))
    }

    search
  }

  def runQuery(query: SearchRequestBuilder): RequestResult = {
    val result = query.execute().get()

    var aggregationNames = Seq[String]()

    def buildResultSet(aggregation: Aggregation, tuple: Seq[Any] = Nil,
                       resultSet: Seq[Seq[Any]]): Seq[Seq[Any]] = {

      aggregationNames = aggregationNames :+ aggregation.getName

      aggregation match {

        case dateHistogram: DateHistogram =>
          dateHistogram.getBuckets.map {
            bucket =>
              if (bucket.getAggregations.isEmpty) {
                resultSet :+ (tuple :+ bucket.getKey :+ bucket.getDocCount)
              } else {
                bucket.getAggregations.map(buildResultSet(_, tuple :+ bucket.getKey, resultSet))
                  .flatten.toSeq
              }
          }.flatten.toSeq

        case cardinality: InternalCardinality =>
          resultSet :+ (tuple :+ cardinality.getValue)

      }
    }

    val resultSet = result.getAggregations.map(buildResultSet(_, Nil, Nil)).flatten.toSeq

    RequestResult(Seq(aggregationNames.distinct:_*), resultSet)
  }

}
