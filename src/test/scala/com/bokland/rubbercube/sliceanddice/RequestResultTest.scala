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

  "All keys from result set are loaded" in {
    RequestResult(Seq(Map("a" -> 1), Map("b" -> 2))).allKeys should be (Seq("a", "b"))
  }

  "Inner sequence of documents is flattened" in {
    RequestResult(Seq(Map("a" -> Map("b" -> Seq(Map("c" -> 1, "d" -> 2), Map("c" -> 3, "e" -> Seq(4, 5)))))))
    .flatResultSet should be(List(Map("a.b.c" -> Seq(1, 3), "a.b.d" -> 2, "a.b.e" -> List(4, 5))))
  }

}
