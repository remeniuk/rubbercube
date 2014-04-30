package com.bokland.rubbercube.cube

import com.bokland.rubbercube.{AggregationType, Dimension}
import com.bokland.rubbercube.filter.Filter
import com.bokland.rubbercube.measure.{DerivedMeasure, Measure}

/**
 * Created by remeniuk on 4/29/14.
 */
case class Cube(id: String, aggregations: Map[Dimension, AggregationType],
  measures: Iterable[Measure], filters: Iterable[Filter] = Nil, parentId: Option[String] = None) {

  def derivedMeasures: Iterable[DerivedMeasure] =
    measures.filter(_.isInstanceOf[DerivedMeasure]).map(_.asInstanceOf[DerivedMeasure])

}