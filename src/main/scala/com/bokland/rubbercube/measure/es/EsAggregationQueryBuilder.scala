package com.bokland.rubbercube.measure.es

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder
import com.bokland.rubbercube.measure.{Measure, AggregationQueryBuilder}
import com.bokland.rubbercube._
import org.elasticsearch.search.aggregations.AggregationBuilders._
import com.bokland.rubbercube.Dimension
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram
import com.bokland.rubbercube.DateAggregationType.DateAggregationType
import com.bokland.rubbercube.measure._

/**
 * Created by remeniuk on 4/29/14.
 */

object EsAggregationQueryBuilder extends AggregationQueryBuilder[AbstractAggregationBuilder] {

  def buildAggregationQuery(measure: Measure, aggregations: Map[Dimension, AggregationType]) = {
    val (aggregationQuery, lowestAggregation) = buildQueryCore(aggregations)

    measure match {

      case CountDistinct(userDimension, alias) =>
        addSubAggregation(lowestAggregation, cardinality(alias.getOrElse(measure.name))
          .field(userDimension.fieldName))

      case Count(userDimension, alias) =>
        addSubAggregation(lowestAggregation, count(alias.getOrElse(measure.name))
          .field(userDimension.fieldName))

      case Sum(userDimension, alias) =>
        addSubAggregation(lowestAggregation, sum(alias.getOrElse(measure.name))
          .field(userDimension.fieldName))

      case Avg(userDimension, alias) =>
        addSubAggregation(lowestAggregation, avg(alias.getOrElse(measure.name))
          .field(userDimension.fieldName))

    }

    aggregationQuery
  }

  ///////////////////////

  private implicit def toEsInterval(dateType: DateAggregationType) =
    dateType match {
      case DateAggregationType.Year => DateHistogram.Interval.YEAR
      case DateAggregationType.Month => DateHistogram.Interval.MONTH
      case DateAggregationType.Week => DateHistogram.Interval.WEEK
      case DateAggregationType.Quarter => DateHistogram.Interval.QUARTER
      case DateAggregationType.Day => DateHistogram.Interval.DAY
    }

  private def buildAggregationQuery(aggregation: (Dimension, AggregationType)): AbstractAggregationBuilder = {
    val (dimension, aggregationType) = aggregation
    aggregationType match {
      case CategoryAggregation => terms(dimension.name).field(dimension.fieldName)
      case DateAggregation(interval) => dateHistogram(dimension.name)
        .interval(interval).field(dimension.fieldName)
    }
  }

  private def addSubAggregation(superAgg: AbstractAggregationBuilder,
                                subAgg: AbstractAggregationBuilder): AbstractAggregationBuilder = {
    val subAggregationMethod = superAgg.getClass.getMethod("subAggregation",
      classOf[AbstractAggregationBuilder])
    subAggregationMethod.invoke(superAgg, subAgg)

    subAgg
  }

  private def buildQueryCore(aggregations: Map[Dimension, AggregationType]):
  (AbstractAggregationBuilder, AbstractAggregationBuilder) = {
    val aggregation: AbstractAggregationBuilder = buildAggregationQuery(aggregations.head)
    var subAgg: AbstractAggregationBuilder = null

    if (aggregations.size > 1) (aggregation /: aggregations.tail) {
      (aggs, agg) =>
        subAgg = buildAggregationQuery(agg)
        addSubAggregation(aggs, subAgg)
        subAgg
    } else {
      subAgg = aggregation
    }

    (aggregation, subAgg)
  }

}
