package com.bokland.rubbercube

import java.util.Date

/**
 * Created by remeniuk on 5/2/14.
 */
object DimensionValue {

  implicit def toDimensionValue(value: Any) = value match {
    case v: Int => LongValue(v.toLong)
    case v: Double => DoubleValue(v)
    case v: Long => LongValue(v)
    case v: String => StringValue(v)
    case v: Date => DateValue(v)
    case v: Seq[String] => SequenceValue(v)
  }

}

trait DimensionValue {
  val value: Any
}

case class LongValue(value: Long) extends DimensionValue

case class DoubleValue(value: Double) extends DimensionValue

case class StringValue(value: String) extends DimensionValue

case class DateValue(value: Date) extends DimensionValue

case class SequenceValue(value: Seq[String]*) extends DimensionValue
