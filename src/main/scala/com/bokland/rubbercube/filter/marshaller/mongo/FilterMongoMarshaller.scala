package com.bokland.rubbercube.filter.marshaller.mongo

import com.bokland.rubbercube.filter._
import com.mongodb.casbah.commons.MongoDBObject
import com.bokland.rubbercube.marshaller.mongo.{MongoMarshaller, DimensionMongoMarshaller}
import com.mongodb.casbah.query.Imports._

/**
 * Created by remeniuk on 5/1/14.
 */
object FilterMongoMarshaller extends MongoMarshaller[Filter] {

  def marshal(obj: Filter): DBObject = {
    obj match {
      case filter: SingleDimension =>
        MongoDBObject("operation" -> filter.getClass.getSimpleName,
          "dimension" -> DimensionMongoMarshaller.marshal(filter.dimension),
          "value" -> filter.value.value)

      case filter: MultiDimensional =>
        MongoDBObject("operation" -> filter.getClass.getSimpleName,
          "filters" -> filter.filters.map(marshal))

    }
  }

  def unmarshal(obj: DBObject): Filter = {
    obj.as[String]("operation") match {
      case "or" => or(obj.as[MongoDBList]("filters").map(obj => unmarshal(obj.asInstanceOf[DBObject])): _*)
      case "and" => and(obj.as[MongoDBList]("filters").map(obj => unmarshal(obj.asInstanceOf[DBObject])): _*)
      case "eql" => eql(DimensionMongoMarshaller.unmarshal(obj.as[DBObject]("dimension")), obj.as[Any]("value"))
      case "neql" => neql(DimensionMongoMarshaller.unmarshal(obj.as[DBObject]("dimension")), obj.as[Any]("value"))
      case "gt" => gt(DimensionMongoMarshaller.unmarshal(obj.as[DBObject]("dimension")), obj.as[Any]("value"))
      case "gte" => gte(DimensionMongoMarshaller.unmarshal(obj.as[DBObject]("dimension")), obj.as[Any]("value"))
      case "lt" => lt(DimensionMongoMarshaller.unmarshal(obj.as[DBObject]("dimension")), obj.as[Any]("value"))
      case "lte" => lte(DimensionMongoMarshaller.unmarshal(obj.as[DBObject]("dimension")), obj.as[Any]("value"))
    }
  }

}
