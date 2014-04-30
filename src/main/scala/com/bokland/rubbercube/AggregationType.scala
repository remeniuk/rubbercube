package com.bokland.rubbercube

import com.bokland.rubbercube.DateAggregationType.DateAggregationType

/**
 * Created by remeniuk on 4/29/14.
 */
trait AggregationType

case object CategoryAggregation extends AggregationType

object DateAggregationType extends Enumeration {

  type DateAggregationType = Value
  val Year, Quarter, Month, Week, Day = Value

}

case class DateAggregation(aggregationType: DateAggregationType) extends AggregationType