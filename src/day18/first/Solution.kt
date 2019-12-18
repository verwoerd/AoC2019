package day18.first


import tools.Coordinate
import tools.adjacentCoordinates
import tools.origin
import tools.priorityQueueOf
import tools.timeSolution

/**
 * @author verwoerd
 * @since 18-12-2019
 */
typealias Maze = MutableMap<Coordinate, MazeTile>

typealias PointsOfInterest = Map<Coordinate, Char>

// key, path, keys on you
typealias SearchTriple = Triple<Char, MutableSet<Char>, Int>

fun main() = timeSolution {
  val keyLocations = mutableMapOf<Char, Coordinate>()
  val doorLocations = mutableMapOf<Char, Coordinate>()
  val poi = mutableMapOf<Coordinate, Char>()
  var startLocation: Coordinate = origin
  val maze = System.`in`.bufferedReader().readLines().mapIndexed { y: Int, value: String ->
    value.toCharArray().mapIndexed { x, char ->
      val coordinate = Coordinate(x, y)
      coordinate to when (char) {
        '#' -> MazeTile.WALL
        '.' -> MazeTile.EMPTY
        '@' -> {
          startLocation = coordinate
          poi[coordinate] = char
          MazeTile.START
        }
        in 'a'..'z' -> {
          keyLocations[char] = coordinate
          poi[coordinate] = char
          MazeTile.KEY
        }
        in 'A'..'Z' -> {
          doorLocations[char] = coordinate
          poi[coordinate] = char
          MazeTile.DOOR
        }
        else -> error("invalid Tile $char")
      }
    }
  }.flatten().toMap().toMutableMap()
  val optionsMap = maze.optionsMap(keyLocations, poi, startLocation)
  println(traverseTheOptions(optionsMap, keyLocations))
}

fun Maze.findKeysRequiredFor(
  coordinate: Coordinate,
  targetCoordinate: Coordinate,
  poi: PointsOfInterest
                            ): Triple<Coordinate, PointsOfInterest, Int>? {
  var queue = listOf<Triple<Coordinate, PointsOfInterest, Int>>(Triple(coordinate, mutableMapOf(), 0))
  val seen = mutableSetOf<Coordinate>()
  var startFound = false
  while (queue.isNotEmpty() && !startFound) {
    queue = queue.flatMap { (current, pointsOfInterest, distance) ->
      adjacentCoordinates(current).filter { seen.add(it) }
        .filter { getValue(it) != MazeTile.WALL }
        .map {
          val newPoi = pointsOfInterest.toMutableMap()
          when (it) {
            targetCoordinate -> startFound = true
            else -> when (getValue(it)) {
              MazeTile.DOOR, MazeTile.KEY -> newPoi[it] = poi.getValue(it)
              else -> Unit
            }
          }
          Triple(it, newPoi, distance + 1)

        }.toList()
    }
  }
  return queue.firstOrNull { it.first == targetCoordinate }
}

fun Maze.optionsMap(keyLocations: Map<Char, Coordinate>, poi: PointsOfInterest, startLocation: Coordinate) =
  keyLocations.mapValues { (_, targetCoordinate) ->
    keyLocations.filter { it.value != targetCoordinate }.mapValues {
      this.findKeysRequiredFor(targetCoordinate, it.value, poi)
    }
  } + ('@' to keyLocations.mapValues {
    findKeysRequiredFor(it.value, startLocation, poi)
  })

fun traverseTheOptions(
  optionsMap: Map<Char, Map<Char, Triple<Coordinate, PointsOfInterest, Int>?>>,
  keyLocations: Map<Char, Coordinate>
                      ): SearchTriple? {
  val filterDoorMap = mutableMapOf<PointsOfInterest, Set<Char>>()
  val filterKeyMap = mutableMapOf<PointsOfInterest, Set<Char>>()

  val seen = mutableSetOf<SearchTriple>()
  // Greedy search for the longest path to create a cutoff point
  val queue = priorityQueueOf<SearchTriple>(
    Comparator { o1, o2 -> -1 * o1.second.size.compareTo(o2.second.size) },
    Triple('@', mutableSetOf(), 0)
                                           )
  var bestPath: SearchTriple? = null
  while (queue.isNotEmpty()) {
    val (current, path, distance) = queue.poll()
    if (distance > bestPath?.third ?: Int.MAX_VALUE) continue
    val options = optionsMap.getValue(current)
      .filter { it.key !in path }
      .filter { candidate ->
        path.containsAll(filterDoorMap.computeIfAbsent(candidate.value!!.second) { key ->
          key.values.filter { it in 'A'..'Z' }.map { it.toLowerCase() }.toSet()
        })
      }
      .map { candidate ->
        Triple(
          candidate.key,
          path.toMutableSet().also { entry ->
            entry.addAll(
              filterKeyMap.computeIfAbsent(
                candidate.value!!.second
                                          ) { key ->
                key.values.filter { it in 'a'..'z' }.toSet()
              })
          }.also { it.add(candidate.key) },
          distance + candidate.value!!.third
              )
      }.filter { it.third < bestPath?.third ?: Int.MAX_VALUE }.filter { seen.add(it) }

    options.filter { it.second.containsAll(keyLocations.keys) }.forEach {
      println("Found possible path $it")
      bestPath = when (bestPath) {
        null -> it
        else -> when {
          it.third < bestPath!!.third -> it
          else -> bestPath
        }
      }
    }
    options
      .toCollection(queue)
  }
  return bestPath
}


enum class MazeTile {
  WALL, EMPTY, START, KEY, DOOR
}
