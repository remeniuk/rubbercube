package com.bokland.rubbercube.measure.kpi

import com.bokland.rubbercube.sliceanddice.AbstractSliceAndDice
import com.bokland.rubbercube.Aggregation
import com.bokland.rubbercube.filter.Filter
import org.elasticsearch.index.query.{FilterBuilder, QueryBuilder}

/**
 * Created by remeniuk on 5/3/14.
 */
trait KPI {

  val alias: String

  def generateQuery(aggregations: Seq[Aggregation], filters: Either[Seq[Filter], FilterBuilder] = Left(Nil)): AbstractSliceAndDice

}
