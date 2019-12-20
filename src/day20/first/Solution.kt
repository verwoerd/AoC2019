package day20.first

import tools.Coordinate
import tools.adjacentCoordinates
import tools.origin
import tools.priorityQueueOf
import tools.timeSolution

const val START = "AA"
const val END = "ZZ"

fun main() = timeSolution {
  val (maze, portals) = parseMaze()
  println(maze.findPath(portals))
}

private fun Maze.findPath(portals: Portals): Int {
  val startCoordinate = portals.getValue(START).first
  val endCoordinate = portals.getValue(END).first
  val seen = mutableSetOf(startCoordinate)
  val portalIndexes = portals.values.filter { it.second != origin }
    .flatMap { listOf(it.first to it.second, it.second to it.first) }
    .toMap()
  val queue = priorityQueueOf(Comparator { o1, o2 -> o1.second.compareTo(o2.second) }, startCoordinate to 0)
  while (queue.isNotEmpty()) {
    val (coordinate, distance) = queue.poll()
    when (coordinate) {
      endCoordinate -> return distance
    }
    adjacentCoordinates(coordinate).filter { seen.add(it) }.filter { (x, y) -> get(y)[x] == '.' }
      .map { target ->
        when (target) {
          in portalIndexes.keys -> portalIndexes.getValue(target) to distance + 2
          else -> target to distance + 1
        }
      }.toCollection(queue)
  }
  error("No path found")
}

typealias Maze = List<CharArray>
typealias Portals = MutableMap<String, Pair<Coordinate, Coordinate>>

fun parseMaze(): Pair<Maze, Portals> {
  val input: Maze = System.`in`.bufferedReader().readLines().map { it.toCharArray() }
  val portals: Portals = mutableMapOf()
  for (y in 2..input.size - 2) {
    if (input[y][2] == ' ') continue
    for (x in 2..input[y].size - 2) {
      when (input[y][x]) {
        '.' -> input.checkPortal(x, y, portals)
      }
    }
  }
  return input to portals
}


private fun Maze.checkPortal(x: Int, y: Int, portals: Portals): Boolean {
  val label = when {
    get(y)[x - 1] in 'A'..'Z' -> "${get(y)[x - 2]}${get(y)[x - 1]}"
    get(y)[x + 1] in 'A'..'Z' -> "${get(y)[x + 1]}${get(y)[x + 2]}"
    get(y - 1)[x] in 'A'..'Z' -> "${get(y - 2)[x]}${get(y - 1)[x]}"
    get(y + 1)[x] in 'A'..'Z' -> "${get(y + 1)[x]}${get(y + 2)[x]}"
    else -> ""
  }
  return when (label) {
    "" -> false
    in portals.keys -> {
      portals[label] = portals.getValue(label).first to Coordinate(x, y)
      true
    }
    else -> {
      portals[label] = Coordinate(x, y) to origin
      true
    }
  }
}
