package com.bokland.rubbercube.filter

import com.bokland.rubbercube.Dimension

/**
 * Created by remeniuk on 4/29/14.
 */
trait Filter {
  val dimension: Dimension
  val value: Any
}

object Filter {

  case class gt(dimension: Dimension, value: Any) extends Filter

  case class gte(dimension: Dimension, value: Any) extends Filter

  case class lt(dimension: Dimension, value: Any) extends Filter

  case class lte(dimension: Dimension, value: Any) extends Filter

  case class eql(dimension: Dimension, value: Any) extends Filter

  case class neql(dimension: Dimension, value: Any) extends Filter

  case class in(dimension: Dimension, value: Any) extends Filter

  case class sequence(dimension: Dimension, value: Seq[String]*) extends Filter

  case class and(filters: Filter*) extends Filter {
    val dimension = ???
    val value = ???
  }

  case class or(filters: Filter*) extends Filter {
    val dimension = ???
    val value = ???
  }

}