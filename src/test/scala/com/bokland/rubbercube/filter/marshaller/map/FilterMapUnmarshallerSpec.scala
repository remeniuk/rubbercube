package com.bokland.rubbercube.filter.marshaller.map

import org.scalatest._
import com.bokland.rubbercube.{SequenceValue, Dimension}
import com.bokland.rubbercube.filter._

/**
 * Created by remeniuk on 5/6/14.
 */
class FilterMapUnmarshallerSpec extends WordSpec with ShouldMatchers with BeforeAndAfterAll {

  "URL" should {

    "be parsed into filter" in {
      val url = "~rd[_][0][]=2013-01-01~rd[_][0][]=2014-01-01~p[_][]=iPad~p[_][]=iPhone~em[_][0][]=1~em[_][0][]=132"

      FilterMapUnmarshaller.fromURL(url) should be(
        and(
          and(
            gte(Dimension("rd"), FilterMapUnmarshaller.DateFormat.parse("2013-01-01")),
            lte(Dimension("rd"), FilterMapUnmarshaller.DateFormat.parse("2014-01-01"))
          ),
          in(Dimension("p"), SequenceValue(List("iPad", "iPhone"))),
          and(
            gte(Dimension("em"), 1.0),
            lte(Dimension("em"), 132.0)
          )
        )
      )
    }

  }

}
