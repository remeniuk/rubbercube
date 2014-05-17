package com.bokland.rubbercube.sliceanddice

import org.scalatest._

/**
 * Created by remeniuk on 5/17/14.
 */
class RequestResultTest extends WordSpec with ShouldMatchers with BeforeAndAfterAll {

  "Multi-level result set is flattened" in {
    RequestResult(Seq(Map("l1" -> 1, "l2" -> Map("l2" -> 2, "l3" -> Map("l3" -> 3)))))
      .flatResultSet should be(List(Map("l1" -> 1, "l2.l2" -> 2, "l2.l3.l3" -> 3)))
  }

}
