package day15.second

import day15.first.Direction
import day15.first.coordinates
import day15.first.endLocation
import day15.first.generateMaze
import tools.timeSolution

fun main() = timeSolution {
  val code = readLine()!!.split(",").mapIndexed { index, it -> index.toLong() to it.toLong() }.toMap()
  generateMaze(code)
  println(floodFill())
}

fun floodFill(): Int {
  val seen = mutableSetOf(endLocation)
  var iteration = -1
  var queue = listOf(endLocation)
  while (queue.isNotEmpty()) {
    iteration++
    queue = queue.flatMap { coordinate ->
      seen.add(coordinate)
      Direction.values().map { it.moveFromCoordinate(coordinate) }.filter { it !in seen }
        .filter { coordinates[it] ?: false }
    }
  }
  return iteration
}
