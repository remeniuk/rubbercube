package com.bokland.rubbercube.filter.marshaller.map

import com.bokland.rubbercube.marshaller.Unmarshaller
import com.bokland.rubbercube.filter._
import com.bokland.rubbercube.{SequenceValue, Dimension}
import java.text.SimpleDateFormat

/**
 * Created by remeniuk on 5/6/14.
 */
object FilterMapUnmarshaller extends Unmarshaller[Map[String, Seq[String]], Filter] {

  private val RangeFilter = "[_][0][]"
  private val CategoryFilter = "[_][]"

  private val RangeFilterEscaped = "\\[_\\]\\[0\\]\\[\\]"
  private val CategoryFilterEscaped = "\\[_\\]\\[\\]"

  private val NumberRegex = "([0-9\\.]+)".r
  private val DateRegex = "[0-9]{4}-[0-9]{2}-[0-9]{2}".r

  private[map] val DateFormat = new SimpleDateFormat("yyyy-mm-dd")

  def unmarshal(obj: Map[String, Seq[String]]): Filter = {
    val filters: Iterable[Filter] = obj map {
      case (key, values) if key.contains(RangeFilter) && values.forall(v => NumberRegex.pattern.matcher(v).matches) =>
        val fieldName = key.replaceAll(RangeFilterEscaped, "")
        and(gte(Dimension(fieldName), values(0).toDouble),
          lte(Dimension(fieldName), values(1).toDouble))

      case (key, values) if key.contains(RangeFilter) && values.forall(v => DateRegex.pattern.matcher(v).matches) =>
        val fieldName = key.replaceAll(RangeFilterEscaped, "")
        and(gte(Dimension(fieldName), DateFormat.parse(values(0))),
          lte(Dimension(fieldName), DateFormat.parse(values(1))))

      case (key, values) if key.contains(CategoryFilter) =>
        val fieldName = key.replaceAll(CategoryFilterEscaped, "")
        in(Dimension(fieldName), SequenceValue(values))
    }

    if(filters.size == 1) filters.head else and(filters.toSeq:_*)
  }

  def fromURL(url: String, paramSeparator: String = "~") = {
    val paramsMultiMap = url.split(paramSeparator).filterNot(_.isEmpty).map {
      keyVal =>
        val Array(key, value) = keyVal.split("=")
        key -> value
    }.groupBy(_._1).map(tuple => tuple._1 -> tuple._2.map(_._2).toList)

    unmarshal(paramsMultiMap)
  }

}
