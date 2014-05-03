package com.bokland.rubbercube.sliceanddice

import com.bokland.rubbercube.{AggregationType, Dimension}
import com.bokland.rubbercube.measure.DerivedMeasure

/**
 * Created by remeniuk on 5/1/14.
 */
case class LeftJoin(queries: Seq[SliceAndDice], by: Seq[Mapping],
                derivedMeasures: Seq[DerivedMeasure] = Nil) extends AbstractSliceAndDice
