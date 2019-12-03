package day3.second

import tools.Coordinate
import tools.origin
import tools.timeSolution
import java.util.PriorityQueue
import kotlin.test.fail

fun slowMain() = timeSolution { // Took 32 seconds
  val points = mutableListOf<Coordinate>()
  var last = origin
  readLine()!!.split(",").forEach { current ->
    // assumption: a circuit does not cross itself
    val size = current.substring(1).toInt()
    last = when (current[0]) {
      'R' -> (last.x + 1..(last.x) + size).map {
        Coordinate(
          it,
          last.y
                  )
      }.toCollection(points).run { Coordinate(last.x + size, last.y) }
      'L' -> (last.x - size until last.x).map {
        Coordinate(
          it,
          last.y
                  )
      }.reversed().toCollection(points).run { Coordinate(last.x - size, last.y) }
      'U' -> (last.y + 1..(last.y) + size).map { Coordinate(last.x, it) }.toCollection(points).run {
        Coordinate(
          last.x,
          last.y + size
                  )
      }
      'D' -> (last.y - size until last.y).map {
        Coordinate(
          last.x,
          it
                  )
      }.reversed().toCollection(points).run { Coordinate(last.x, last.y - size) }
      else -> fail("Illegal direction $current")
    }
  }
  points.remove(origin)
  last = origin
  val intersections = PriorityQueue<Int>()
  var index = 1 // compensate for initial circuit to be off by 1
  readLine()!!.split(",").forEach { current ->
    val size = current.substring(1).toInt()
    last = when (current[0]) {
      'R' -> (last.x + 1..(last.x) + size).map { Pair(++index, Coordinate(it, last.y)) }.filter { it.second in points }
        .map { it.first + points.indexOf(it.second) }.toCollection(intersections).run {
          Coordinate(
            last.x + size,
            last.y
                    )
        }
      'L' -> (last.x - size until last.x).reversed().map {
        Pair(
          ++index,
          Coordinate(it, last.y)
            )
      }.filter { it.second in points }
        .map { it.first + points.indexOf(it.second) }.toCollection(intersections).run {
          Coordinate(
            last.x - size,
            last.y
                    )
        }
      'U' -> (last.y + 1..(last.y) + size).map { Pair(++index, Coordinate(last.x, it)) }.filter { it.second in points }
        .map { it.first + points.indexOf(it.second) }.toCollection(intersections).run {
          Coordinate(
            last.x,
            last.y + size
                    )
        }
      'D' -> (last.y - size until last.y).reversed().map {
        Pair(
          ++index,
          Coordinate(last.x, it)
            )
      }.filter { it.second in points }
        .map { it.first + points.indexOf(it.second) }.toCollection(intersections).run {
          Coordinate(
            last.x,
            last.y - size
                    )
        }
      else -> fail("Illegal direction $current")
    }
  }
  println(intersections.peek())
}

fun main() = timeSolution {
  // reduced to 200ms
  val points = mutableSetOf<Coordinate>()
  val pointsTimeIndexes = mutableMapOf<Coordinate, Int>()
  var last = origin
  var index = 0

  readLine()!!.split(",").forEach { current ->
    // assumption: a circuit does not cross itself
    val size = current.substring(1).toInt()
    last = when (current[0]) {
      'R' -> (last.x + 1..(last.x) + size).map {
        Pair(
          Coordinate(it, last.y),
          ++index
            )
      }.filter { points.add(it.first) }.associateByTo(pointsTimeIndexes, { it.first }, { it.second })
        .run { Coordinate(last.x + size, last.y) }
      'L' -> (last.x - size until last.x).map { Pair(Coordinate(it, last.y), ++index) }.reversed().filter {
        points.add(
          it.first
                  )
      }
        .associateByTo(pointsTimeIndexes, { it.first }, { it.second }).run { Coordinate(last.x - size, last.y) }
      'U' -> (last.y + 1..(last.y) + size).map { Pair(Coordinate(last.x, it), ++index) }.filter { points.add(it.first) }
        .associateByTo(pointsTimeIndexes, { it.first }, { it.second }).run { Coordinate(last.x, last.y + size) }
      'D' -> (last.y - size until last.y).map { Pair(Coordinate(last.x, it), ++index) }.reversed().filter {
        points.add(
          it.first
                  )
      }
        .associateByTo(pointsTimeIndexes, { it.first }, { it.second }).run { Coordinate(last.x, last.y - size) }
      else -> fail("Illegal direction $current")
    }
  }
  points.remove(origin)
  last = origin
  val intersections = PriorityQueue<Int>()
  index = 0
  readLine()!!.split(",").forEach { current ->
    val size = current.substring(1).toInt()
    last = when (current[0]) {
      'R' -> (last.x + 1..(last.x) + size).map { Pair(++index, Coordinate(it, last.y)) }.filter { it.second in points }
        .map { it.first + pointsTimeIndexes[it.second]!! }.toCollection(intersections).run {
          Coordinate(
            last.x + size,
            last.y
                    )
        }
      'L' -> (last.x - size until last.x).reversed().map {
        Pair(
          ++index,
          Coordinate(it, last.y)
            )
      }.filter { it.second in points }
        .map { it.first + pointsTimeIndexes[it.second]!! }.toCollection(intersections).run {
          Coordinate(
            last.x - size,
            last.y
                    )
        }
      'U' -> (last.y + 1..(last.y) + size).map { Pair(++index, Coordinate(last.x, it)) }.filter { it.second in points }
        .map { it.first + pointsTimeIndexes[it.second]!! }.toCollection(intersections).run {
          Coordinate(
            last.x,
            last.y + size
                    )
        }
      'D' -> (last.y - size until last.y).reversed().map {
        Pair(
          ++index,
          Coordinate(last.x, it)
            )
      }.filter { it.second in points }
        .map { it.first + pointsTimeIndexes[it.second]!! }.toCollection(intersections).run {
          Coordinate(
            last.x,
            last.y - size
                    )
        }
      else -> fail("Illegal direction $current")
    }
  }
  println(intersections.peek())
}
