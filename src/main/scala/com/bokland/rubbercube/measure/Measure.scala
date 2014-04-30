package com.bokland.rubbercube.measure

import com.bokland.rubbercube.Dimension

/**
 * Created by remeniuk on 4/29/14.
 */
trait Measure {

  val dimension: Dimension

  def name = s"${getClass.getSimpleName.toLowerCase}-${dimension.fqn}"

}
