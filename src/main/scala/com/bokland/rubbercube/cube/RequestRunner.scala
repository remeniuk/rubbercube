package com.bokland.rubbercube.cube

import com.bokland.rubbercube.measure.DerivedMeasure

/**
 * Created by remeniuk on 4/29/14.
 */
trait RequestRunner[RequestType] {

  def runQuery(request: RequestType): RequestResult

  def applyDerivedMeasures(derivedMeasures: Iterable[DerivedMeasure])(result: RequestResult): RequestResult

}
