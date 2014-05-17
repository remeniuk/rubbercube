package com.bokland.rubbercube.sliceanddice

import scala.collection.JavaConverters._
import java.util.{List => JList}
import java.util.{Map => JMap}

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
                         delimiter: String = "."): Map[String, Any] = {
    val prefixWithDelimiter = prefix.map(_ + delimiter).getOrElse("")
    map.flatMap {
      case (key, value: Map[String, Any]) =>
        flattenMap(value, Some(s"$prefixWithDelimiter$key"))

      case (key, value: Seq[Any]) =>
        value.head match {
          case _: Map[String, Any] =>
            val res = value.map(tuple => flattenMap(tuple.asInstanceOf[Map[String, Any]], Some(s"$prefixWithDelimiter$key")))
            res.flatMap(_.keys).map {
              key =>
                val v = res.flatMap(_.get(key))
                key -> (if(v.size == 1) v.head else v)
            }

          case _ =>
            Map(s"$prefixWithDelimiter$key" -> value.distinct)
        }

      // Java cases
      case (key, value: JMap[String, Any]) =>
        flattenMap(value.asScala.toMap, Some(s"$prefixWithDelimiter$key"))

      case (key, value: JList[Any]) =>
        value.asScala.head match {
          case _: JMap[String, Any] =>
            val res = value.asScala.map(tuple => flattenMap(tuple.asInstanceOf[JMap[String, Any]].asScala.toMap,
              Some(s"$prefixWithDelimiter$key")))
            res.flatMap(_.keys).map {
              key =>
                val v = res.flatMap(_.get(key))
                key -> (if(v.size == 1) v.head else v)
            }
          case _ =>
            Map(s"$prefixWithDelimiter$key" -> value.asScala.distinct)
        }

      case (key, value) => Map(s"$prefixWithDelimiter$key" -> value)
    }
  }

  lazy val (allKeys, flatResultSet): (Seq[String], Seq[Map[String, Any]]) = {
    val rs = resultSet.map(flattenMap(_))
    (rs.flatMap(_.keys).distinct, rs)
  }

}
