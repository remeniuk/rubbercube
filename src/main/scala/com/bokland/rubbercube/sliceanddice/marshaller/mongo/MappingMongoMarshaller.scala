package com.bokland.rubbercube.sliceanddice.marshaller.mongo

import com.bokland.rubbercube.marshaller.mongo.{DimensionMongoMarshaller, MongoMarshaller}
import com.bokland.rubbercube.sliceanddice.Mapping
import com.mongodb.casbah.query.Imports
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.query.Imports._

/**
 * Created by remeniuk on 5/3/14.
 */
object MappingMongoMarshaller extends MongoMarshaller[Mapping] {

  def marshal(obj: Mapping): Imports.DBObject =
    MongoDBObject("dimensions" -> obj.dimensions.map(DimensionMongoMarshaller.marshal))

  def unmarshal(obj: Imports.DBObject): Mapping =
    Mapping(obj.as[MongoDBList]("dimensions")
      .map(dim => DimensionMongoMarshaller.unmarshal(dim.asInstanceOf[DBObject])))

}
