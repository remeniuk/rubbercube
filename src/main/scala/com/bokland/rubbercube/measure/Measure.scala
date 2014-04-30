package com.bokland.rubbercube.measure

import com.bokland.rubbercube.Dimension

/**
 * Created by remeniuk on 4/29/14.
 */
trait Measure {

  def dimension: Dimension

  def name = s"${getClass.getSimpleName.toLowerCase}-${dimension.name}"

}

trait DerivedMeasure extends Measure {

  def dimension: Dimension = ???

  val measures: Seq[Measure]

  override def name = s"${getClass.getSimpleName.toLowerCase}-${measures.map(_.name).mkString("-")}"

}

