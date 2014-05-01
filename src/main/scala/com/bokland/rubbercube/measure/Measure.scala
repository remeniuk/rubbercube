package com.bokland.rubbercube.measure

import com.bokland.rubbercube.Dimension

/**
 * Created by remeniuk on 4/29/14.
 */
trait Measure {

  def dimension: Dimension

  def name = alias.getOrElse(s"${getClass.getSimpleName.toLowerCase}-${dimension.name}")

  def alias: Option[String]

}

case class MeasureReference(_alias: String) extends Measure {

  val alias = Some(_alias)

  def dimension: Dimension = ???

}

trait DerivedMeasure extends Measure {

  def dimension: Dimension = ???

  val measures: Seq[Measure]

  def alias: Option[String]

  override def name = alias.getOrElse(s"${getClass.getSimpleName.toLowerCase}-${measures.map(_.name).mkString("-")}")

}

