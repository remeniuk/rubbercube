package com.bokland.rubbercube.kpi

import com.bokland.rubbercube.{AggregationType, Dimension}

/**
 * Created by remeniuk on 4/29/14.
 */
trait AggregationQueryBuilder[QueryType] {

    def buildAggregationQuery(kpi: Kpi, aggregations: Map[Dimension, AggregationType]): QueryType

}
