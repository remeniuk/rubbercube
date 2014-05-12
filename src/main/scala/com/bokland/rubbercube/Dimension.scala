package com.bokland.rubbercube

import java.util.Date

/**
 * Created by remeniuk on 4/29/14.
 */
case class Dimension(fieldName: String, cubeId: Option[String] = None,
                     alias: Option[String] = None, defaults: Option[Defaults] = None,
                     valueType: Option[String] = None, dictionary: Map[String, String] = Map()) {

  val name = alias.getOrElse(fieldName.replaceAll("\\.", "_"))

}

trait Defaults

case class DateRangeDefaults(from: Date, to: Date) extends Defaults

case class NumberRangeDefaults(from: Double, to: Double) extends Defaults

case class CategoriesDefaults(values: Seq[String]) extends Defaults