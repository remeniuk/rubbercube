package com.bokland.rubbercube.cube

import com.bokland.rubbercube.{AggregationType, Dimension}
import com.bokland.rubbercube.filter.Filter
import com.bokland.rubbercube.kpi.Kpi

/**
 * Created by remeniuk on 4/29/14.
 */
case class Cube(id: String, aggregations: Map[Dimension, AggregationType],
  kpis: Iterable[Kpi], filters: Iterable[Filter])