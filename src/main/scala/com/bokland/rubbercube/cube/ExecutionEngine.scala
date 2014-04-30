package com.bokland.rubbercube.cube


/**
 * Created by remeniuk on 4/29/14.
 */
trait ExecutionEngine[RequestType] extends RequestBuilder[RequestType]
with RequestRunner[RequestType] {

  def execute(cube: Cube): RequestResult = {
    (buildRequest _ andThen runQuery _)(cube)
  }

}
