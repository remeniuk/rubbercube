package com.bokland.rubbercube.sliceanddice

import com.bokland.rubbercube.Dimension

/**
 * Created by remeniuk on 5/3/14.
 */
object Mapping {

  implicit def toMapping(dimensions: Seq[Dimension]) = Mapping(dimensions)

}

case class Mapping(dimensions: Seq[Dimension])
