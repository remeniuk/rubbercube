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

object EsAggregationQueryBuilder extends AggregationQueryBuilder[(AbstractAggregationBuilder, Map[String, String])] {

  def buildAggregationQuery(measure: Measure, aggregationToFieldName: Map[String, String]) = {
    val aggregationName = measure.alias.getOrElse(measure.name)

    (measure match {
      case CountDistinct(userDimension, alias) => cardinality(aggregationName).field(userDimension.fieldName)
      case Count(userDimension, alias) => count(aggregationName).field(userDimension.fieldName)
      case Sum(userDimension, alias) => sum(aggregationName).field(userDimension.fieldName)
      case Avg(userDimension, alias) => avg(aggregationName).field(userDimension.fieldName)
      case Max(userDimension, alias) => max(aggregationName).field(userDimension.fieldName)
      case Min(userDimension, alias) => min(aggregationName).field(userDimension.fieldName)
      case Categories(userDimension, alias) => terms(aggregationName).field(userDimension.fieldName)
    }, aggregationToFieldName/* + (aggregationName -> measure.dimension.fieldName)*/)
  }

  def buildAggregationQuery(measures: Seq[Measure], aggregations: Seq[Aggregation], aggregationToFieldName: Map[String, String] = Map()) = {
    val (aggregationQuery, lowestAggregation, coreMapping) = buildQueryCore(aggregations, aggregationToFieldName)

    val updatedMapping = (coreMapping /: measures){
      (mapping, measure) =>
        val (query, updatedMapping) = buildAggregationQuery(measure, mapping)
        addSubAggregation(lowestAggregation, query)
        updatedMapping
    }

    (aggregationQuery, updatedMapping)
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

  private def buildAggregationQuery(aggregation: Aggregation, aggregationToFieldName: Map[String, String]):
  (AbstractAggregationBuilder, Map[String, String]) = {
    val Aggregation(dimension, aggregationType) = aggregation
    (aggregationType match {
      case CategoryAggregation => terms(dimension.name).field(dimension.fieldName)
      case NumberAggregation(interval) => histogram(dimension.name)
        .interval(interval).field(dimension.fieldName)
      case DateAggregation(interval) => dateHistogram(dimension.name)
        .interval(interval).field(dimension.fieldName)
    }, aggregationToFieldName + (dimension.name -> aggregation.dimension.fieldName))
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

  private def buildQueryCore(aggregations: Seq[Aggregation], aggregationToFieldName: Map[String, String] = Map()):
  (AbstractAggregationBuilder, AbstractAggregationBuilder, Map[String, String]) = {
    val (aggregation, updatedMapping) = buildAggregationQuery(aggregations.head, aggregationToFieldName)
    var subAgg: AbstractAggregationBuilder = null
    var sumMap = Map[String, String]()

    if (aggregations.size > 1) (aggregation /: aggregations.tail) {
      (aggs, agg) =>
        val (s, m) = buildAggregationQuery(agg, sumMap)
        subAgg = s
        sumMap = m
        addSubAggregation(aggs, subAgg)
        subAgg
    } else {
      subAgg = aggregation
    }

    (aggregation, subAgg, (updatedMapping ++ sumMap).toMap)
  }

}
