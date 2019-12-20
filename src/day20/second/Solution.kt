package day20.second

import day20.first.Maze
import day20.first.Portals
import day20.first.parseMaze
import tools.timeSolution

fun main() = timeSolution {
  val (maze, portals) = parseMaze()
  println(maze.findPath(portals))
}

private fun Maze.findPath(portals: Portals): Int {
  val startCoordinate = portals.getValue(day20.first.START).first
  val endCoordinate = portals.getValue(day20.first.END).first
  val seen = mutableSetOf(0 to startCoordinate)
  val portalIndexes = portals.values.filter { it.second != tools.origin }
    .flatMap { listOf(it.first to it.second, it.second to it.first) }
    .toMap()
  val yMax = indexOfLast { it.contains('#') }
  val xMax = get(yMax).indexOfLast { it == '#' }
  val outerPortals = portalIndexes.keys.partition { (x, y) -> x == 2 || x == xMax || y == 2 || y == yMax }.first

  // greedily search the paths with lower levels first
  val queue = tools.priorityQueueOf(
    Comparator { o1, o2 ->
      when (val result = o1.third.compareTo(o2.third)) {
        0 -> o1.second.compareTo(o2.second)
        else -> result
      }
    },
    Triple(startCoordinate, 0, 0)
                                   )
  while (queue.isNotEmpty()) {
    val (coordinate, distance, level) = queue.poll()
    when {
      coordinate == endCoordinate && level == 0 -> return distance
    }
    tools.adjacentCoordinates(coordinate).filter { seen.add(level to it) }
      .filter { (x, y) -> get(y)[x] == '.' }
      .filter { level != 0 || it !in outerPortals }
      .map { target ->
        when (target) {
          in portalIndexes.keys -> Triple(
            portalIndexes.getValue(target),
            distance + 2,
            when (target) {
              in outerPortals -> level - 1
              else -> level + 1
            }
                                         )
          else -> Triple(target, distance + 1, level)
        }
      }.toCollection(queue)
  }
  error("No path found")
}


