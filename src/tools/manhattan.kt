package tools

import kotlin.math.abs

/**
 * @author verwoerd
 * @since 3-12-2019
 */
data class Coordinate(val x: Int, val y: Int) : Comparable<Coordinate> {
  override fun compareTo(other: Coordinate): Int = when (val result = this.y.compareTo(other.y)) {
    0 -> this.x.compareTo(other.x)
    else -> result
  }

}

fun manhattanDistance(a: Coordinate, b: Coordinate) = abs(a.x - b.x) + abs(a.y - b.y)
val origin = Coordinate(0, 0)

val manhattanOriginComparator = Comparator { a: Coordinate, b: Coordinate ->
  manhattanDistance(origin, a) - manhattanDistance(
    origin, b
                                                  )
}

fun <V> Map<Coordinate, V>.yRange() = keys.minBy { it.y }!!.y to keys.maxBy { it.y }!!.y

fun <V> Map<Coordinate, V>.xRange() = keys.minBy { it.x }!!.x to keys.maxBy { it.x }!!.x
