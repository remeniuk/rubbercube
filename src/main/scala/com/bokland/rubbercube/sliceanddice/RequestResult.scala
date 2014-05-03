package com.bokland.rubbercube.sliceanddice


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

}
