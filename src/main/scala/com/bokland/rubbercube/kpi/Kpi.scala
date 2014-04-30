package com.bokland.rubbercube.kpi

import com.bokland.rubbercube.{AggregationType, Dimension}

/**
 * Created by remeniuk on 4/29/14.
 */
trait Kpi {

  def name = getClass.getSimpleName.toLowerCase

}
