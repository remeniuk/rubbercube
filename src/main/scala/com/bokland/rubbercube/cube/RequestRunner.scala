package com.bokland.rubbercube.cube

/**
 * Created by remeniuk on 4/29/14.
 */
trait RequestRunner[RequestType] {

  def runQuery(request: RequestType): RequestResult

}
