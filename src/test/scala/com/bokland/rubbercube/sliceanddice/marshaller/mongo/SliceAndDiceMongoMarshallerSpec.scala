package com.bokland.rubbercube.sliceanddice.marshaller.mongo

import org.scalatest._
import com.bokland.rubbercube.sliceanddice.SliceAndDice
import com.bokland.rubbercube.{CategoryAggregation, DateAggregationType, DateAggregation, Dimension}
import com.bokland.rubbercube.measure.Sum
import com.bokland.rubbercube.filter.{gt, eql}
import java.util.Date

/**
 * Created by remeniuk on 5/2/14.
 */
class SliceAndDiceMongoMarshallerSpec extends WordSpec with ShouldMatchers with BeforeAndAfterAll {

  val testQuery = SliceAndDice("purchase",
    Seq(Dimension("date") -> CategoryAggregation, Dimension("platform") -> DateAggregation(DateAggregationType.Day)),
    Seq(Sum(Dimension("amount"), alias = Some("daily_revenue"))),
    Seq(eql(Dimension("registration_date"), new Date()),
      gt(Dimension("logins_count"), 1)), Some("user"))

  "SliceAndDice" should {
    "be marshaller to and unmarshalled from DB object" in {
      val marshalledQuery = SliceAndDiceMongoMarshaller.marshal(testQuery)
      SliceAndDiceMongoMarshaller.unmarshal(marshalledQuery) should be(testQuery)
    }

    "be marshalled to JSON and back" in {
      val sliceAndDiceJson = SliceAndDiceMongoMarshaller.toJson(testQuery)
      println("SliceAndDice query: " + sliceAndDiceJson)
      SliceAndDiceMongoMarshaller.fromJson(sliceAndDiceJson) should be(testQuery)
    }
  }

}
