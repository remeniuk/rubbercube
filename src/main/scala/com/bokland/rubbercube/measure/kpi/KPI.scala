package com.bokland.rubbercube.measure.kpi

import com.bokland.rubbercube.sliceanddice.AbstractSliceAndDice
import com.bokland.rubbercube.Aggregation
import com.bokland.rubbercube.filter.Filter

/**
 * Created by remeniuk on 5/3/14.
 */
trait KPI {

  val alias: String

  def generateQuery(aggregations: Seq[Aggregation], filters: Seq[Filter] = Nil): AbstractSliceAndDice

}
