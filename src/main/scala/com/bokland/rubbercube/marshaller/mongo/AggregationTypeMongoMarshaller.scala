package com.bokland.rubbercube.marshaller.mongo

import com.bokland.rubbercube.marshaller.{Unmarshaller, Marshaller}
import com.bokland.rubbercube.{DateAggregationType, DateAggregation, CategoryAggregation, AggregationType}
import com.mongodb.casbah.query.Imports._
import com.mongodb.casbah.query.Imports

/**
 * Created by remeniuk on 5/1/14.
 */
object AggregationTypeMongoMarshaller extends Marshaller[AggregationType, DBObject]
with Unmarshaller[DBObject, AggregationType] {

  def marshal(obj: AggregationType): Imports.DBObject =
    obj match {
      case CategoryAggregation =>
        MongoDBObject("aggregation_type" -> "category")
      case DateAggregation(dateType) =>
        MongoDBObject(
          "aggregation_type" -> "date",
          "date_type" -> dateType.toString
        )
    }

  def unmarshal(obj: Imports.DBObject): AggregationType =
    obj.as[String]("aggregation_type") match {
      case "category" => CategoryAggregation
      case "date" => DateAggregation(DateAggregationType.withName(obj.as[String]("date_type")))
    }

}
