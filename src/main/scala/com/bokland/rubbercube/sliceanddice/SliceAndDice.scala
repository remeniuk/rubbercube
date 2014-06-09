package com.bokland.rubbercube.sliceanddice

import com.bokland.rubbercube.Aggregation
import com.bokland.rubbercube.filter.Filter
import com.bokland.rubbercube.measure.{DerivedMeasure, Measure}
import org.elasticsearch.index.query.{FilterBuilder, QueryBuilder}

/**
 * Created by remeniuk on 4/29/14.
 */
trait AbstractSliceAndDice

case class SliceAndDice(id: String, aggregations: Seq[Aggregation] = Nil,
                        measures: Seq[Measure] = Nil, 
                        filter: Either[Seq[Filter], FilterBuilder] = Left(Nil),
                        parentId: Option[String] = None, from: Int = 0, size: Int = 0,
                        excludeFields: Seq[String] = Nil)
  extends AbstractSliceAndDice {

  def derivedMeasures: Iterable[DerivedMeasure] =
    measures.filter(_.isInstanceOf[DerivedMeasure]).map(_.asInstanceOf[DerivedMeasure])

}