package com.bokland.rubbercube.filter.marshaller.mongo

import com.bokland.rubbercube._
import com.mongodb.casbah.query.Imports._
import com.mongodb.casbah.query.Imports
import com.bokland.rubbercube.DateAggregation
import com.bokland.rubbercube.marshaller.mongo.MongoMarshaller

/**
 * Created by remeniuk on 5/2/14.
 */
object AggregationTypeMongoMarshaller extends MongoMarshaller[AggregationType] {

  def marshal(obj: AggregationType): Imports.DBObject =
    obj match {
      case CategoryAggregation =>
        MongoDBObject("type" -> "category")
      case DateAggregation(dateType) =>
        MongoDBObject("type" -> "date", "date_type" -> dateType.toString)
    }

  def unmarshal(obj: Imports.DBObject): AggregationType =
    obj.as[String]("type") match {
      case "category" => CategoryAggregation
      case "date" => DateAggregation(DateAggregationType.withName(obj.as[String]("date_type")))
    }

}
