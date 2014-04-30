package com.bokland.rubbercube.marshaller.es

import com.bokland.rubbercube.filter.Filter
import Filter._
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders._
import com.bokland.rubbercube.filter.Filter
import com.bokland.rubbercube.Marshaller

/**
 * Created by remeniuk on 4/29/14.
 */
object EsFilterMarshaller extends Marshaller[Filter, QueryBuilder] {

  private val MAX_SLOP = Integer.MAX_VALUE

  def marshal(filter: Filter): QueryBuilder = {
    filter match {
      case f: and =>
        val parentQuery = boolQuery()
        f.filters.foreach {
          case subFilter: neql => parentQuery.mustNot(marshal(subFilter))
          case subFilter =>
            Option(marshal(subFilter))
              .foreach(parentQuery.must)
        }
        parentQuery

      case f: or => f.filters.map(marshal)
        val parentQuery = boolQuery()
        f.filters.foreach(subFilter => parentQuery.should(marshal(subFilter)))
        parentQuery

      case f: gt =>
        rangeQuery(f.dimension.name).from(f.value).includeLower(false)

      case f: gte =>
        rangeQuery(f.dimension.name).from(f.value).includeLower(true)

      case f: lt =>
        rangeQuery(f.dimension.name).to(f.value).includeLower(false)

      case f: lte =>
        rangeQuery(f.dimension.name).to(f.value).includeLower(true)

      case f: in =>
        val subQuery = boolQuery()
        f.value.asInstanceOf[Iterable[Any]].foreach {
          value =>
            subQuery.must(matchQuery(f.dimension.name, value))
        }
        subQuery

      case p: sequence =>
        if (p.value.forall(_.forall(_ == ""))) null
        else {

          val parentQuery = spanOrQuery()
          p.value.foreach {
            sequence =>
              parentQuery.clause((spanNearQuery().slop(MAX_SLOP).inOrder(true) /: sequence) {
                (query, regexTerm) =>
                  query.clause(spanMultiTermQueryBuilder(regexpQuery(p.dimension.name, regexTerm)))
              })
          }

          parentQuery
        }

      case other =>
        matchQuery(filter.dimension.name, filter.value)
    }
  }

}
