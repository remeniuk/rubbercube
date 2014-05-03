package com.bokland.rubbercube.sliceanddice

import com.bokland.rubbercube.measure.DerivedMeasure
import com.bokland.rubbercube.Dimension

/**
 * Created by remeniuk on 4/29/14.
 */
trait RequestRunner[RequestType] {

  def runQuery(request: RequestType): RequestResult

  def applyDerivedMeasures(derivedMeasures: Iterable[DerivedMeasure])(result: RequestResult): RequestResult

  def joinResults(by: Seq[Mapping])(resultSets: Iterable[RequestResult]): RequestResult

}
