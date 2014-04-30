package com.bokland.rubbercube.cube

/**
 * Created by remeniuk on 4/29/14.
 */
trait RequestBuilder[RequestType] {

  def buildRequest(cube: Cube): RequestType

}
