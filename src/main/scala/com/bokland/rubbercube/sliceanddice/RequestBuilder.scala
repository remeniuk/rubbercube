package com.bokland.rubbercube.sliceanddice

/**
 * Created by remeniuk on 4/29/14.
 */
trait RequestBuilder[RequestType] {

  def buildRequest(sliceAndDice: SliceAndDice): RequestType

}
