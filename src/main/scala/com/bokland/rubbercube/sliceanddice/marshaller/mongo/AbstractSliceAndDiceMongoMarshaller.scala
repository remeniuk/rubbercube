package com.bokland.rubbercube.sliceanddice.marshaller.mongo

import com.bokland.rubbercube.sliceanddice.{LeftJoin, AbstractSliceAndDice, SliceAndDice}
import com.mongodb.casbah.commons.MongoDBObject
import com.bokland.rubbercube.filter.marshaller.mongo.{FilterMongoMarshaller, AggregationMongoMarshaller}
import com.bokland.rubbercube.measure.marshaller.mongo.MeasureMongoMarshaller
import com.mongodb.casbah.query.Imports._
import com.bokland.rubbercube.marshaller.mongo.MongoMarshaller
import com.bokland.rubbercube.measure.DerivedMeasure

/**
 * Created by remeniuk on 5/1/14.
 */
object AbstractSliceAndDiceMongoMarshaller extends MongoMarshaller[AbstractSliceAndDice] {

  def marshal(obj: AbstractSliceAndDice): DBObject = {
    val dbObject = MongoDBObject.newBuilder

    obj match {
      case sliceAndDice: SliceAndDice =>
        dbObject += "type" -> "sliceAndDice"
        dbObject += "cube" -> sliceAndDice.id
        dbObject += "aggregations" -> sliceAndDice.aggregations.map(AggregationMongoMarshaller.marshal)
        dbObject += "measures" -> sliceAndDice.measures.map(MeasureMongoMarshaller.marshal)
        dbObject += "filters" -> sliceAndDice.filters.map(FilterMongoMarshaller.marshal)
        sliceAndDice.parentId.foreach(parentId => dbObject += "parent_id" -> parentId)

      case leftJoin: LeftJoin =>
        dbObject += "type" -> "leftJoin"
        dbObject += "queries" -> leftJoin.queries.map(marshal)
        dbObject += "by" -> leftJoin.by.map(MappingMongoMarshaller.marshal)
        dbObject += "measures" -> leftJoin.derivedMeasures.map(MeasureMongoMarshaller.marshal)

    }

    dbObject.result()
  }

  def unmarshal(obj: DBObject): AbstractSliceAndDice =
    obj.as[String]("type") match {
      case "sliceAndDice" =>
        SliceAndDice(obj.as[String]("cube"),
          obj.as[MongoDBList]("aggregations").map(aggregation =>
            AggregationMongoMarshaller.unmarshal(aggregation.asInstanceOf[DBObject])),
          obj.as[MongoDBList]("measures").map(aggregation =>
            MeasureMongoMarshaller.unmarshal(aggregation.asInstanceOf[DBObject])),
          obj.as[MongoDBList]("filters").map(aggregation =>
            FilterMongoMarshaller.unmarshal(aggregation.asInstanceOf[DBObject])),
          obj.getAs[String]("parent_id"))

      case "leftJoin" =>
        LeftJoin(obj.as[MongoDBList]("queries").map(query =>
          unmarshal(query.asInstanceOf[DBObject]).asInstanceOf[SliceAndDice]),
          obj.as[MongoDBList]("by").map(query =>
            MappingMongoMarshaller.unmarshal(query.asInstanceOf[DBObject])),
          obj.as[MongoDBList]("measures").map(query =>
            MeasureMongoMarshaller.unmarshal(query.asInstanceOf[DBObject]).asInstanceOf[DerivedMeasure])
        )
    }

}
