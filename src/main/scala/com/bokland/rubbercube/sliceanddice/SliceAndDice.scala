package com.bokland.rubbercube.sliceanddice

import com.bokland.rubbercube.{Aggregation, AggregationType, Dimension}
import com.bokland.rubbercube.filter.Filter
import com.bokland.rubbercube.measure.{DerivedMeasure, Measure}

/**
 * Created by remeniuk on 4/29/14.
 */
case class SliceAndDice(id: String, aggregations: Seq[Aggregation],
  measures: Seq[Measure], filters: Seq[Filter] = Nil, parentId: Option[String] = None) {

  def derivedMeasures: Iterable[DerivedMeasure] =
    measures.filter(_.isInstanceOf[DerivedMeasure]).map(_.asInstanceOf[DerivedMeasure])

}