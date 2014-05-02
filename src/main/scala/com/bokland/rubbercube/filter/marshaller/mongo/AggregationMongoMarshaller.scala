package com.bokland.rubbercube.filter.marshaller.mongo

import com.mongodb.casbah.query.Imports._
import com.bokland.rubbercube.Aggregation
import com.mongodb.casbah.query.Imports
import com.bokland.rubbercube.marshaller.mongo.{MongoMarshaller, DimensionMongoMarshaller}

/**
 * Created by remeniuk on 5/2/14.
 */
object AggregationMongoMarshaller extends MongoMarshaller[Aggregation] {

  def marshal(obj: Aggregation): Imports.DBObject = {
    val builder = MongoDBObject.newBuilder

    builder += "dimension" -> DimensionMongoMarshaller.marshal(obj.dimension)
    builder += "aggregation" -> AggregationTypeMongoMarshaller.marshal(obj.aggregationType)

    builder.result()
  }

  def unmarshal(obj: Imports.DBObject): Aggregation =
    Aggregation(DimensionMongoMarshaller.unmarshal(obj.as[DBObject]("dimension")),
      AggregationTypeMongoMarshaller.unmarshal(obj.as[DBObject]("aggregation")))

}
