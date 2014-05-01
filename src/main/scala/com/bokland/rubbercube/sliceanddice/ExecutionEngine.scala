package com.bokland.rubbercube.sliceanddice


/**
 * Created by remeniuk on 4/29/14.
 */
trait ExecutionEngine[RequestType] extends RequestBuilder[RequestType]
with RequestRunner[RequestType] {

  def execute(sliceAndDice: SliceAndDice): RequestResult = {
    (buildRequest _ andThen runQuery andThen applyDerivedMeasures(sliceAndDice.derivedMeasures))(sliceAndDice)
  }

  def execute(join: LeftJoin): RequestResult = {
    (joinResults(join.by) _ andThen applyDerivedMeasures(join.derivedMeasures))(join.queries.map(execute))
  }

}
