package day10.first

import tools.Coordinate
import tools.timeSolution
import kotlin.math.atan2

fun main() = timeSolution {
  val map = readAsteroidsMap()
  val best = bestStationLocation(map)
  println("${best.first.size}  ${best.second}")

}

fun readAsteroidsMap() = System.`in`.bufferedReader().readLines().mapIndexed { y, s ->
  s.mapIndexedNotNull { x, c ->
    when (c) {
      '#' -> Coordinate(x, y)
      else -> null
    }
  }
}.flatten().toSet()

fun bestStationLocation(map: Set<Coordinate>) = map.map {
  // create a set of all the angles relative to all other asteroids
  map.filter { c -> it != c }.fold(mutableSetOf<Double>()) { acc, coordinate ->
    val xDiff = (it.x - coordinate.x).toDouble()
    val yDiff = (it.y - coordinate.y).toDouble()
    acc.also { acc += atan2(xDiff, yDiff) }
  } to it
}.maxBy { it.first.size }!!
