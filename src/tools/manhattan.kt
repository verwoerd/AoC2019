package tools

import kotlin.math.abs

/**
 * @author verwoerd
 * @since 3-12-2019
 */
data class Coordinate(val x: Int, val y: Int)

fun manhattanDistance(a: Coordinate, b: Coordinate) = abs(a.x - b.x) + abs(a.y - b.y)
val origin = Coordinate(0, 0)

val manhattanOriginComparator = Comparator {a:Coordinate,b: Coordinate -> manhattanDistance(origin, a) - manhattanDistance(
  origin, b)}
