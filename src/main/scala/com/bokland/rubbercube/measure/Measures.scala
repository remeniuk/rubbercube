package com.bokland.rubbercube.measure

import com.bokland.rubbercube.Dimension

/**
 * Created by remeniuk on 4/30/14.
 */
object Measures {

  case class CountDistinct(dimension: Dimension, alias: Option[String] = None) extends Measure

  case class Count(dimension: Dimension, alias: Option[String] = None) extends Measure

  case class Sum(dimension: Dimension, alias: Option[String] = None) extends Measure

  case class Avg(dimension: Dimension, alias: Option[String] = None) extends Measure

}

object DerivedMeasures {

  case class Div(m1: Measure, m2: Measure, alias: Option[String] = None) extends DerivedMeasure {

    val measures: Seq[Measure] = Seq(m1, m2)

  }

}
