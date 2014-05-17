package com.bokland.rubbercube.filter

import com.bokland.rubbercube.{DimensionValue, Dimension}

/**
 * Created by remeniuk on 4/29/14.
 */
trait Filter {

  def cubeId: Option[String]

}

trait SingleDimension {
  self: Filter =>

  val dimension: Dimension
  val value: DimensionValue

  def cubeId: Option[String] = dimension.cubeId
}

trait MultiDimensional {
  self: Filter =>

  val filters: Seq[Filter]

  def cubeId: Option[String] = filters.head.cubeId
}

case object EmptyFilter extends Filter {
  def cubeId: Option[String] = ???
}