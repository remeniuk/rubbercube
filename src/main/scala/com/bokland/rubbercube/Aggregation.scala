package com.bokland.rubbercube

/**
 * Created by remeniuk on 5/2/14.
 */
object Aggregation {

  implicit def toAggregation(aggregation: (Dimension, AggregationType)) =
    Aggregation(aggregation._1, aggregation._2)

}

case class Aggregation(dimension: Dimension, aggregationType: AggregationType)
