package com.bokland.rubbercube.sliceanddice.marshaller.mongo

import org.scalatest._
import com.bokland.rubbercube.sliceanddice.{LeftJoin, SliceAndDice}
import com.bokland.rubbercube.{CategoryAggregation, DateAggregationType, DateAggregation, Dimension}
import com.bokland.rubbercube.measure.{MeasureReference, Div, CountDistinct, Sum}
import com.bokland.rubbercube.filter.{gt, eql}
import java.util.Date

/**
 * Created by remeniuk on 5/2/14.
 */
class SliceAndDiceMongoMarshallerSpec extends WordSpec with ShouldMatchers with BeforeAndAfterAll {

  val testQuery = SliceAndDice("purchase",
    Seq(Dimension("date") -> CategoryAggregation, Dimension("platform") -> DateAggregation(DateAggregationType.Day)),
    Seq(Sum(Dimension("amount"), alias = Some("daily_revenue"))),
    Left(Seq(eql(Dimension("registration_date"), new Date()),
      gt(Dimension("logins_count"), 1))), Some("user"))

  val revenuePerDay = SliceAndDice("purchase",
    Seq(Dimension("date") -> DateAggregation(DateAggregationType.Day)),
    Seq(Sum(Dimension("amount"), alias = Some("daily_revenue"))))

  val onlinePerDay = SliceAndDice("session",
    Seq(Dimension("date") -> DateAggregation(DateAggregationType.Day)),
    Seq(CountDistinct(Dimension("_parent"), alias = Some("dau"))))

  val testLeftJoin = LeftJoin(
    queries = Seq(revenuePerDay, onlinePerDay),
    by = Seq(Seq(Dimension("date", Some("purchase")), Dimension("date", Some("session")))),
    derivedMeasures = Seq(Div(MeasureReference("daily_revenue"), MeasureReference("dau"), Some("arppdau"))))

  "SliceAndDice" should {
    "be marshaller to and unmarshalled from DB object" in {
      val marshalledQuery = AbstractSliceAndDiceMongoMarshaller.marshal(testQuery)
      AbstractSliceAndDiceMongoMarshaller.unmarshal(marshalledQuery) should be(testQuery)
    }

    "be marshalled to JSON and back" in {
      val sliceAndDiceJson = AbstractSliceAndDiceMongoMarshaller.toJson(testQuery)
      println("SliceAndDice query: " + sliceAndDiceJson)
      AbstractSliceAndDiceMongoMarshaller.fromJson(sliceAndDiceJson) should be(testQuery)
    }
  }

  "LeftJoin" should {
    "be marshaller to and unmarshalled from DB object" in {
      val marshalledQuery = AbstractSliceAndDiceMongoMarshaller.marshal(testLeftJoin)
      AbstractSliceAndDiceMongoMarshaller.unmarshal(marshalledQuery) should be(testLeftJoin)
    }

    "be marshalled to JSON and back" in {
      val sliceAndDiceJson = AbstractSliceAndDiceMongoMarshaller.toJson(testLeftJoin)
      println("LeftJoin query: " + sliceAndDiceJson)
      AbstractSliceAndDiceMongoMarshaller.fromJson(sliceAndDiceJson) should be(testLeftJoin)
    }
  }

}
