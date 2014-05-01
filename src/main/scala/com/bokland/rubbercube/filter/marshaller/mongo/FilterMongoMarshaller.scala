package com.bokland.rubbercube.filter.marshaller.mongo

import com.bokland.rubbercube.marshaller.{Unmarshaller, Marshaller}
import com.bokland.rubbercube.filter._
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import com.bokland.rubbercube.marshaller.mongo.DimensionMongoMarshaller

/**
 * Created by remeniuk on 5/1/14.
 */
object FilterMongoMarshaller extends Marshaller[Filter, DBObject]
with Unmarshaller[DBObject, Filter] {

  def marshal(obj: Filter): DBObject = {
    obj match {
      case filter: SingleDimension =>
        MongoDBObject("operation" -> filter.getClass.getSimpleName,
          "dimension" -> DimensionMongoMarshaller.marshal(filter.dimension))

      case filter: MultiDimensional =>
        MongoDBObject("operation" -> filter.getClass.getSimpleName,
          "filters" -> filter.filters.map(marshal))

    }

    ???
  }

  def unmarshal(obj: DBObject): Filter = ???

}
