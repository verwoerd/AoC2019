package day3.first

import tools.Coordinate
import tools.manhattanDistance
import tools.manhattanOriginComparator
import tools.origin
import tools.timeSolution
import java.util.PriorityQueue
import kotlin.test.fail

fun main() = timeSolution {
  val points = mutableSetOf<Coordinate>()
  var last = origin
  // Find all the points the first circuit go to
  readLine()!!.split(",").forEach { current ->
    // assumption: a circuit does not cross itself
    val size = current.substring(1).toInt()
    last = when (current[0]) {
      'R' -> (last.x..(last.x) + size).map {
        Coordinate(
          it,
          last.y
                  )
      }.toCollection(points).run { Coordinate(last.x + size, last.y) }
      'L' -> (last.x - size..last.x).map { Coordinate(it, last.y) }.toCollection(points).run {
        Coordinate(
          last.x - size,
          last.y
                  )
      }
      'U' -> (last.y..(last.y) + size).map { Coordinate(last.x, it) }.toCollection(points).run {
        Coordinate(
          last.x,
          last.y + size
                  )
      }
      'D' -> (last.y - size..last.y).map { Coordinate(last.x, it) }.toCollection(points).run {
        Coordinate(
          last.x,
          last.y - size
                  )
      }
      else -> fail("Illegal direction $current")
    }
  }
  points.remove(origin)
  // find all the points that the second circuit cross with the first circuit
  last = origin
  val intersections = PriorityQueue<Coordinate>(manhattanOriginComparator)
  readLine()!!.split(",").forEach { current ->
    val size = current.substring(1).toInt()
    last = when (current[0]) {
      'R' -> (last.x..(last.x) + size).map {
        Coordinate(
          it,
          last.y
                  )
      }.filter { it in points }.toCollection(intersections).run { Coordinate(last.x + size, last.y) }
      'L' -> (last.x - size..last.x).map {
        Coordinate(
          it,
          last.y
                  )
      }.filter { it in points }.toCollection(intersections).run { Coordinate(last.x - size, last.y) }
      'U' -> (last.y..(last.y) + size).map {
        Coordinate(
          last.x,
          it
                  )
      }.filter { it in points }.toCollection(intersections).run { Coordinate(last.x, last.y + size) }
      'D' -> (last.y - size..last.y).map {
        Coordinate(
          last.x,
          it
                  )
      }.filter { it in points }.toCollection(intersections).run { Coordinate(last.x, last.y - size) }
      else -> fail("Illegal direction $current")
    }
  }
  println(manhattanDistance(Coordinate(0, 0), intersections.peek()))
}

