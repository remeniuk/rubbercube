package com.bokland.rubbercube.filter.marshaller.mongo

import com.bokland.rubbercube._
import com.mongodb.casbah.query.Imports._
import com.mongodb.casbah.query.Imports
import com.bokland.rubbercube.marshaller.mongo.MongoMarshaller
import com.bokland.rubbercube.DateAggregation

/**
 * Created by remeniuk on 5/2/14.
 */
object AggregationTypeMongoMarshaller extends MongoMarshaller[AggregationType] {

  def marshal(obj: AggregationType): Imports.DBObject =
    obj match {
      case CategoryAggregation =>
        MongoDBObject("type" -> AggregationType.Category)
      case DateAggregation(dateType) =>
        MongoDBObject("type" -> AggregationType.Date, "date_type" -> dateType.toString)
      case NumberAggregation(interval) =>
        MongoDBObject("type" -> AggregationType.Number, "interval" -> interval)
    }

  def unmarshal(obj: Imports.DBObject): AggregationType =
    obj.as[String]("type") match {
      case AggregationType.Category => CategoryAggregation
      case AggregationType.Date => DateAggregation(DateAggregationType.withName(obj.as[String]("date_type")))
      case AggregationType.Number => NumberAggregation(obj.as[Int]("interval"))
    }

}
