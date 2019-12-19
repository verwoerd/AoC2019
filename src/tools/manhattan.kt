package tools

import kotlin.math.abs

/**
 * @author verwoerd
 * @since 3-12-2019
 */
data class Coordinate(val x: Int, val y: Int) : Comparable<Coordinate> {
  override fun compareTo(other: Coordinate): Int = when (val result = y.compareTo(other.y)) {
    0 -> x.compareTo(other.x)
    else -> result
  }

  operator fun minus(other: Coordinate): Coordinate = Coordinate(x - other.x, y - other.y)
  operator fun plus(other: Coordinate): Coordinate = Coordinate(x + other.x, y + other.y)
  operator fun plus(i: Number): Coordinate = plus(Coordinate(i.toInt(), i.toInt()))
  operator fun times(i: Number): Coordinate = this * Coordinate(i.toInt(), i.toInt())
  operator fun times(other: Coordinate): Coordinate = Coordinate(x * other.x, y * other.y)
  operator fun div(other: Coordinate): Coordinate = Coordinate(x / other.x, y / other.y)
  infix fun plusY(i: Number): Coordinate = plus(Coordinate(0, i.toInt()))
  infix fun plusX(i: Number): Coordinate = plus(Coordinate(i.toInt(), 0))
}


val xIncrement = Coordinate(1, 0)
val yIncrement = Coordinate(0, 1)

fun manhattanDistance(a: Coordinate, b: Coordinate) = abs(a.x - b.x) + abs(a.y - b.y)
val origin = Coordinate(0, 0)

val manhattanOriginComparator = Comparator { a: Coordinate, b: Coordinate ->
  manhattanDistance(origin, a) - manhattanDistance(
    origin, b
                                                  )
}

fun <V> Map<Coordinate, V>.yRange() = keys.minBy { it.y }!!.y to keys.maxBy { it.y }!!.y

fun <V> Map<Coordinate, V>.xRange() = keys.minBy { it.x }!!.x to keys.maxBy { it.x }!!.x

fun adjacentCoordinates(origin: Coordinate) = sequenceOf(
  Coordinate(origin.x + 1, origin.y),
  Coordinate(origin.x - 1, origin.y),
  Coordinate(origin.x, origin.y + 1),
  Coordinate(origin.x, origin.y - 1)
                                                        )
