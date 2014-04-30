package com.bokland.rubbercube.cube.es

import org.elasticsearch.action.search.SearchRequestBuilder
import com.bokland.rubbercube.cube.{RequestResult, Cube, ExecutionEngine}
import org.elasticsearch.client.transport.TransportClient
import com.bokland.rubbercube.marshaller.es.EsFilterMarshaller
import com.bokland.rubbercube.measure.es.EsAggregationQueryBuilder
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram
import scala.collection.JavaConversions._
import org.elasticsearch.search.aggregations.Aggregation
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality
import org.elasticsearch.index.query.QueryBuilders._
import org.elasticsearch.search.aggregations.metrics.sum.Sum
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount

/**
 * Created by remeniuk on 4/29/14.
 */
class EsExecutionEngine(client: TransportClient, index: String) extends ExecutionEngine[SearchRequestBuilder] {

  def buildRequest(cube: Cube): SearchRequestBuilder = {
    import cube._

    val search = client.prepareSearch(index).setTypes(cube.id).setSize(0)

    measures.foreach {
      measure => search.addAggregation(EsAggregationQueryBuilder.buildAggregationQuery(measure, aggregations))
    }

    if (filters.size > 0) {

      val query = boolQuery()

      val parentFilters = for {
        parentCubeId <- cube.parentId.toIterable
        filter <- filters
        filterCubeId <- filter.dimension.cubeId if filterCubeId == parentCubeId
      } yield filter

      filters.toList.diff(parentFilters.toList).foreach {
        filter => query.must(EsFilterMarshaller.marshal(filter))
      }

      parentFilters.foreach {
        filter =>
          query.must(hasParentQuery(filter.dimension.cubeId.get,
            EsFilterMarshaller.marshal(filter)))
      }

      search.setQuery(query)

    }

    search
  }

  def runQuery(query: SearchRequestBuilder): RequestResult = {
    val result = query.execute().get()

    def buildResultSet(aggregation: Aggregation, tuple: Map[String, Any] = Map(),
                       resultSet: Seq[Map[String, Any]] = Nil): Seq[Map[String, Any]] = {

      aggregation match {

        case dateHistogram: DateHistogram =>
          dateHistogram.getBuckets.map {
            bucket =>
                bucket.getAggregations.map(buildResultSet(_, tuple + (aggregation.getName -> bucket.getKey), resultSet))
                  .flatten.toSeq
          }.flatten.toSeq

        case cardinality: Cardinality => resultSet :+ (tuple + (aggregation.getName -> cardinality.getValue))

        case sum: Sum => resultSet :+ (tuple + (aggregation.getName -> sum.getValue))

        case count: ValueCount => resultSet :+ (tuple + (aggregation.getName -> count.getValue))

      }
    }

    val resultSet = result.getAggregations.map(buildResultSet(_)).flatten.toSeq

    RequestResult(resultSet)
  }

}
