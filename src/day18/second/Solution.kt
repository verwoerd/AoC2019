package day18.second

import day18.first.Maze
import day18.first.MazeTile
import day18.first.PointsOfInterest
import day18.first.findKeysRequiredFor
import tools.Coordinate
import tools.origin
import tools.priorityQueueOf
import tools.timeSolution

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

  maze[Coordinate(startLocation.x + 1, startLocation.y + 1)] = MazeTile.START
  maze[Coordinate(startLocation.x + 1, startLocation.y - 1)] = MazeTile.START
  maze[Coordinate(startLocation.x - 1, startLocation.y - 1)] = MazeTile.START
  maze[Coordinate(startLocation.x - 1, startLocation.y + 1)] = MazeTile.START
  maze[Coordinate(startLocation.x, startLocation.y)] = MazeTile.WALL
  maze[Coordinate(startLocation.x + 1, startLocation.y)] = MazeTile.WALL
  maze[Coordinate(startLocation.x - 1, startLocation.y)] = MazeTile.WALL
  maze[Coordinate(startLocation.x, startLocation.y + 1)] = MazeTile.WALL
  maze[Coordinate(startLocation.x, startLocation.y - 1)] = MazeTile.WALL
  val optionsMap = maze.optionsMapWith4Starts(keyLocations, poi, startLocation)
  println(traverseTheOptionsWithRobots(optionsMap, keyLocations))
}

typealias OptionsMap = Map<Char, Map<Char, Triple<Coordinate, PointsOfInterest, Int>?>>

fun Maze.optionsMapWith4Starts(keyLocations: Map<Char, Coordinate>, poi: PointsOfInterest, startLocation: Coordinate) =
  keyLocations.mapValues { (_, targetCoordinate) ->
    keyLocations.filter { it.value != targetCoordinate }.mapValues {
      this.findKeysRequiredFor(targetCoordinate, it.value, poi)
    }
  } + ('1' to keyLocations.mapValues {
    findKeysRequiredFor(it.value, Coordinate(startLocation.x + 1, startLocation.y + 1), poi)
  }) + ('2' to keyLocations.mapValues {
    findKeysRequiredFor(it.value, Coordinate(startLocation.x + 1, startLocation.y - 1), poi)
  }) + ('3' to keyLocations.mapValues {
    findKeysRequiredFor(it.value, Coordinate(startLocation.x - 1, startLocation.y - 1), poi)
  }) + ('4' to keyLocations.mapValues {
    findKeysRequiredFor(it.value, Coordinate(startLocation.x - 1, startLocation.y + 1), poi)
  })

typealias SearchTriple = Triple<Robots, Set<Char>, Int>

val filterDoorMap = mutableMapOf<PointsOfInterest, Set<Char>>()
val filterKeyMap = mutableMapOf<PointsOfInterest, Set<Char>>()
val seen = mutableSetOf<SearchTriple>()

fun traverseTheOptionsWithRobots(
  optionsMap: OptionsMap,
  keyLocations: Map<Char, Coordinate>
                                ): SearchTriple? {
  val queue = priorityQueueOf<SearchTriple>(
    Comparator { o1, o2 -> o1.second.size.compareTo(o2.second.size) },
    Triple(
      Robots(
        Robot('1', 0, mutableSetOf()),
        Robot('2', 0, mutableSetOf()),
        Robot('3', 0, mutableSetOf()),
        Robot('4', 0, mutableSetOf())
            ), mutableSetOf(), 0
          )
                                           )
  var bestPath: SearchTriple? = null

  while (queue.isNotEmpty()) {
    val (robots, path, distance) = queue.poll()
    if (distance > bestPath?.third ?: Int.MAX_VALUE) continue
    val options = optionsMap.possiblePaths(robots.robot1.copy(), path)
      .map { robot ->
        Triple(
          robots.copy(robot1 = robot),
          path.toMutableSet().also { it.addAll(robot.path) },
          distance + robot.distance - robots.robot1.distance
              )
      }
      .toMutableList() + optionsMap.possiblePaths(robots.robot2.copy(), path)
      .map { robot ->
        Triple(
          robots.copy(robot2 = robot),
          path.toMutableSet().also { it.addAll(robot.path) },
          distance + robot.distance - robots.robot2.distance
              )
      }
      .toMutableList() + optionsMap.possiblePaths(robots.robot3.copy(), path)
      .map { robot ->
        Triple(
          robots.copy(robot3 = robot),
          path.toMutableSet().also { it.addAll(robot.path) },
          distance + robot.distance - robots.robot3.distance
              )
      }
      .toMutableList() + optionsMap.possiblePaths(robots.robot4.copy(), path)
      .map { robot ->
        Triple(
          robots.copy(robot4 = robot),
          path.toMutableSet().also { it.addAll(robot.path) },
          distance + robot.distance - robots.robot4.distance
              )
      }

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

    options.filter { seen.add(it) }
      .toCollection(queue)
  }
  return bestPath
}

fun OptionsMap.possiblePaths(robot: Robot, path: Set<Char>) =
  getValue(robot.location)
    .filterValues { it != null }
    .filter { it.key !in path }
    .filter { candidate ->
      path.containsAll(filterDoorMap.computeIfAbsent(candidate.value!!.second) { key ->
        key.values.filter { it in 'A'..'Z' }.map { it.toLowerCase() }.toSet()
      })
    }
    .map { candidate ->
      Robot(
        candidate.key,
        robot.distance + candidate.value!!.third,
        robot.path.toMutableSet().also { entry ->
          entry.addAll(
            filterKeyMap.computeIfAbsent(
              candidate.value!!.second
                                        ) { key ->
              key.values.filter { it in 'a'..'z' }.toSet()
            })
        }.also { it.add(candidate.key) }
           )
    }

data class Robot(var location: Char, var distance: Int, var path: Set<Char>)

data class Robots(
  val robot1: Robot,
  val robot2: Robot,
  val robot3: Robot,
  val robot4: Robot
                 )
