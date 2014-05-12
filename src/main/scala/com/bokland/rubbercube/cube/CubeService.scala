package com.bokland.rubbercube.cube

import com.bokland.rubbercube._
import com.bokland.rubbercube.filter.Filter
import com.bokland.rubbercube.sliceanddice.ExecutionEngine
import com.bokland.rubbercube.CategoriesDefaults
import com.bokland.rubbercube.measure.Max
import com.bokland.rubbercube.sliceanddice.SliceAndDice
import com.bokland.rubbercube.NumberRangeDefaults
import com.bokland.rubbercube.Dimension
import scala.Some
import com.bokland.rubbercube.measure.Min
import com.bokland.rubbercube.measure.Categories
import java.util.Date

/**
 * Created by remeniuk on 5/11/14.
 */
trait CubeService[T] {

  val executionEngine: ExecutionEngine[T]

  private def numberRange(cubeId: String, dimension: Dimension,
                          filters: Seq[Filter] = Nil) = {
    val result = executionEngine.execute(SliceAndDice(cubeId, Nil, Seq(Max(dimension, Some("max")),
      Min(dimension, Some("min"))), filters))

    for {
      rs <- result.resultSet.headOption
      min <- rs.get("min")
      max <- rs.get("max")
    } yield NumberRangeDefaults(min.asInstanceOf[Double], max.asInstanceOf[Double])
  }

  def withDefaults(cubeId: String, dimension: Dimension,
                          filters: Seq[Filter] = Nil): Dimension = {
    val defaults = dimension.valueType match {
      case Some(AggregationType.Category) =>
        val result = executionEngine.execute(SliceAndDice(cubeId, Nil,
          Seq(Categories(dimension, Some("categories"))), filters))

        val categories = for {
          rs <- result.resultSet
          category <- rs.get("categories")
        } yield category.asInstanceOf[String]

        if(categories.isEmpty) None
        else Some(CategoriesDefaults(categories))

      case Some(AggregationType.Number) =>
        numberRange(cubeId, dimension, filters)

      case Some(AggregationType.Date) =>
        numberRange(cubeId, dimension, filters).map {
          case NumberRangeDefaults(min, max) => DateRangeDefaults(new Date(min.toLong), new Date(max.toLong))
        }
    }

    dimension.copy(defaults = defaults)
  }

}
