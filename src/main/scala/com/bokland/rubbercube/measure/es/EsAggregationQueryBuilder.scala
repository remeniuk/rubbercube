package com.bokland.rubbercube.measure.es

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder
import com.bokland.rubbercube._
import org.elasticsearch.search.aggregations.AggregationBuilders._
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram
import com.bokland.rubbercube.measure._
import com.bokland.rubbercube.measure.Sum
import com.bokland.rubbercube.DateAggregation
import com.bokland.rubbercube.measure.Count
import com.bokland.rubbercube.measure.CountDistinct
import com.bokland.rubbercube.measure.Avg
import com.bokland.rubbercube.measure.Categories
import com.bokland.rubbercube.DateAggregationType.DateAggregationType
import com.bokland.rubbercube.DateAggregationType

/**
 * Created by remeniuk on 4/29/14.
 */

object EsAggregationQueryBuilder extends AggregationQueryBuilder[AbstractAggregationBuilder] {

  def buildAggregationQuery(measure: Measure, aggregations: Seq[Aggregation]) = {
    val (aggregationQuery, lowestAggregation) = if (aggregations.size > 0) buildQueryCore(aggregations) else (null, null)

    val subAggregation = measure match {

      case CountDistinct(userDimension, alias) =>
        cardinality(alias.getOrElse(measure.name)).field(userDimension.fieldName)

      case Count(userDimension, alias) =>
        count(alias.getOrElse(measure.name)).field(userDimension.fieldName)

      case Sum(userDimension, alias) =>
        sum(alias.getOrElse(measure.name)).field(userDimension.fieldName)

      case Avg(userDimension, alias) =>
        avg(alias.getOrElse(measure.name)).field(userDimension.fieldName)

      case Max(userDimension, alias) =>
        max(alias.getOrElse(measure.name)).field(userDimension.fieldName)

      case Min(userDimension, alias) =>
        min(alias.getOrElse(measure.name)) .field(userDimension.fieldName)

      case Categories(userDimension, alias) =>
        terms(alias.getOrElse(measure.name)).field(userDimension.fieldName)

    }

    if(lowestAggregation != null) {
      addSubAggregation(lowestAggregation, subAggregation)
      aggregationQuery
    } else subAggregation
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

  private def buildAggregationQuery(aggregation: Aggregation): AbstractAggregationBuilder = {
    val Aggregation(dimension, aggregationType) = aggregation
    aggregationType match {
      case CategoryAggregation => terms(dimension.name).field(dimension.fieldName)
      case DateAggregation(interval) => dateHistogram(dimension.name)
        .interval(interval).field(dimension.fieldName)
    }
  }

  private def addSubAggregation(superAgg: AbstractAggregationBuilder,
                                subAgg: AbstractAggregationBuilder): AbstractAggregationBuilder = {
    if (superAgg == null) {
      subAgg
    } else {
      val subAggregationMethod = superAgg.getClass.getMethod("subAggregation",
        classOf[AbstractAggregationBuilder])
      subAggregationMethod.invoke(superAgg, subAgg)

      subAgg
    }
  }

  private def buildQueryCore(aggregations: Seq[Aggregation]):
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
