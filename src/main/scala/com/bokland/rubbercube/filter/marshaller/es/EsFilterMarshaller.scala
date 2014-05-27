package com.bokland.rubbercube.marshaller.es

import com.bokland.rubbercube.filter._
import org.elasticsearch.index.query.{QueryBuilders, FilterBuilders, QueryBuilder}
import org.elasticsearch.index.query.QueryBuilders._
import com.bokland.rubbercube.filter.Filter
import com.bokland.rubbercube.marshaller.Marshaller
import com.bokland.rubbercube.StringValue

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
        rangeQuery(f.dimension.fieldName).from(f.value.value).includeLower(false)

      case f: gte =>
        rangeQuery(f.dimension.fieldName).from(f.value.value).includeLower(true)

      case f: lt =>
        rangeQuery(f.dimension.fieldName).to(f.value.value).includeLower(false)

      case f: lte =>
        rangeQuery(f.dimension.fieldName).to(f.value.value).includeLower(true)

      case f: in =>
        val subQuery = boolQuery()
        f.value.value.asInstanceOf[Iterable[Any]].foreach {
          value =>
            subQuery.should(matchQuery(f.dimension.fieldName, value))
        }
        subQuery

      case script(StringValue(value), _) =>
        QueryBuilders.filteredQuery(matchAllQuery(), FilterBuilders.scriptFilter(value))

      case p: sequence =>
        if (p.value.value.forall(_.forall(_ == ""))) null
        else {

          val parentQuery = spanOrQuery()
          parentQuery.clause((spanNearQuery().slop(MAX_SLOP).inOrder(true) /: p.value.value) {
            (query, regexTerm) =>
              query.clause(spanMultiTermQueryBuilder(regexpQuery(p.dimension.fieldName, regexTerm)))
          })

          parentQuery
        }

      case other: SingleDimension =>
        matchQuery(other.dimension.fieldName, other.value.value)
    }
  }

}
