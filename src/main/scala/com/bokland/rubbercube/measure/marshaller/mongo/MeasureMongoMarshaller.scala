package com.bokland.rubbercube.measure.marshaller.mongo

import com.bokland.rubbercube.marshaller.{Unmarshaller, Marshaller}
import com.bokland.rubbercube.measure._
import com.mongodb.casbah.commons.MongoDBObject
import com.bokland.rubbercube.measure.MeasureReference
import com.bokland.rubbercube.measure.CountDistinct
import com.bokland.rubbercube.marshaller.mongo.{MongoMarshaller, DimensionMongoMarshaller}
import com.mongodb.casbah.query.Imports._

/**
 * Created by remeniuk on 5/1/14.
 */
object MeasureMongoMarshaller extends MongoMarshaller[Measure] {

  def marshal(obj: Measure): DBObject = {
    val builder = MongoDBObject.newBuilder

    obj match {

      case MeasureReference(alias) =>
        builder +=("type" -> "reference", "alias" -> alias)

      case CountDistinct(dimension, alias) =>
        builder +=("type" -> "dimension", "operation" -> "countdistinct",
          "dimension" -> DimensionMongoMarshaller.marshal(dimension))
        alias.foreach(a => builder += "alias" -> a)

      case Sum(dimension, alias) =>
        builder +=("type" -> "dimension", "operation" -> "sum",
          "dimension" -> DimensionMongoMarshaller.marshal(dimension))
        alias.foreach(a => builder += "alias" -> a)

      case Avg(dimension, alias) =>
        builder +=("type" -> "dimension", "operation" -> "avg",
          "dimension" -> DimensionMongoMarshaller.marshal(dimension))
        alias.foreach(a => builder += "alias" -> a)

      case Div(dim1, dim2, alias) =>
        builder +=("type" -> "derived", "operation" -> "div",
          "dim1" -> marshal(dim1), "dim2" -> marshal(dim2))
        alias.foreach(a => builder += "alias" -> a)

    }

    builder.result()
  }

  def unmarshal(obj: DBObject): Measure = {
    obj.as[String]("type") match {
      case "reference" =>
        MeasureReference(obj.as[String]("alias"))

      case "dimension" =>
        obj.as[String]("operation") match {

          case "countdistinct" =>
            CountDistinct(DimensionMongoMarshaller.unmarshal(obj.as[DBObject]("dimension")),
              obj.getAs[String]("alias"))

          case "count" =>
            Count(DimensionMongoMarshaller.unmarshal(obj.as[DBObject]("dimension")),
              obj.getAs[String]("alias"))

          case "sum" =>
            Sum(DimensionMongoMarshaller.unmarshal(obj.as[DBObject]("dimension")),
              obj.getAs[String]("alias"))

          case "avg" =>
            Avg(DimensionMongoMarshaller.unmarshal(obj.as[DBObject]("dimension")),
              obj.getAs[String]("alias"))

        }

      case "derived" =>
        obj.as[String]("operation") match {

          case "div" =>
            Div(unmarshal(obj.as[DBObject]("dim1")), unmarshal(obj.as[DBObject]("dim2")),
              obj.getAs[String]("alias"))

        }

    }
  }

}
