package com.bokland.rubbercube.sliceanddice

import scala.collection.JavaConverters._

/**
 * Created by remeniuk on 4/29/14.
 */
object RequestResult {

  def joinTuples(tuples: Seq[Map[String, Any]]) = tuples.reduce(_ ++ _)

}

case class RequestResult(resultSet: Seq[Map[String, Any]], cubeId: Option[String] = None) {

  def find(query: Map[String, Any]): Option[Map[String, Any]] = {
    resultSet.find(_.toList.containsSlice(query.toList))
  }

  private def flattenMap(map: Map[String, Any], prefix: Option[String] = None,
                         delimeter: String = "."): Map[String, Any] = {
    val prefixWithDelimeter = prefix.map(_ + delimeter).getOrElse("")
    map.flatMap {
      case (key, value: Map[String, Any]) =>
        flattenMap(value, Some(s"$prefixWithDelimeter$key"))
      case (key, value: java.util.Map[String, Any]) =>
        flattenMap(value.asScala.toMap, Some(s"$prefixWithDelimeter$key"))
      case (key, value) => Map(s"$prefixWithDelimeter$key" -> value)
    }
  }

  lazy val flatResultSet: Seq[Map[String, Any]] = resultSet.map(flattenMap(_))

}
