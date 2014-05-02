package com.bokland.rubbercube.sliceanddice.marshaller.mongo

import com.bokland.rubbercube.sliceanddice.SliceAndDice
import com.mongodb.casbah.commons.MongoDBObject
import com.bokland.rubbercube.filter.marshaller.mongo.{FilterMongoMarshaller, AggregationMongoMarshaller}
import com.bokland.rubbercube.measure.marshaller.mongo.MeasureMongoMarshaller
import com.mongodb.casbah.query.Imports._
import com.bokland.rubbercube.marshaller.mongo.MongoMarshaller

/**
 * Created by remeniuk on 5/1/14.
 */
object SliceAndDiceMongoMarshaller extends MongoMarshaller[SliceAndDice] {

  def marshal(obj: SliceAndDice): DBObject = {
    val dbObject = MongoDBObject.newBuilder

    dbObject += "cube" -> obj.id
    dbObject += "aggregations" -> obj.aggregations.map(AggregationMongoMarshaller.marshal)
    dbObject += "measures" -> obj.measures.map(MeasureMongoMarshaller.marshal)
    dbObject += "filters" -> obj.filters.map(FilterMongoMarshaller.marshal)
    obj.parentId.foreach(parentId => dbObject += "parent_id" -> parentId)

    dbObject.result()
  }

  def unmarshal(obj: DBObject): SliceAndDice =
    SliceAndDice(obj.as[String]("cube"),
      obj.as[MongoDBList]("aggregations").map(aggregation =>
        AggregationMongoMarshaller.unmarshal(aggregation.asInstanceOf[DBObject])),
      obj.as[MongoDBList]("measures").map(aggregation =>
        MeasureMongoMarshaller.unmarshal(aggregation.asInstanceOf[DBObject])),
      obj.as[MongoDBList]("filters").map(aggregation =>
        FilterMongoMarshaller.unmarshal(aggregation.asInstanceOf[DBObject])),
      obj.getAs[String]("parent_id"))

}
