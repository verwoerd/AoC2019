package day24.second

import day24.first.ErisTile
import day24.first.ErisTile.BUG
import day24.first.ErisTile.EMPTY
import day24.first.toTile
import tools.Coordinate
import tools.adjacentCoordinates
import tools.timeSolution
import tools.xRange
import tools.yRange

const val MINUTES = 200
val CENTER = Coordinate(2, 2)
val EMPTY_MAZE = emptyMaze()

typealias ErisMaze = Map<Coordinate, ErisTile>
typealias LevelMap = Map<Int, ErisMaze>

fun main() = timeSolution {
  val maze = System.`in`.bufferedReader().readLines()
    .mapIndexed { y, line -> line.toCharArray().mapIndexed { x, char -> Coordinate(x, y) to char.toTile() } }.flatten()
    .toMap().withDefault { EMPTY }
  var time = 0
  var currentMaxLevel = 1 // inside start zone
  var currentMinLevel = -1 // outside start zone

  var levelMap = mutableMapOf(0 to maze, -1 to emptyMaze(), 1 to emptyMaze()).withDefault { EMPTY_MAZE }


  while (time < MINUTES) {
    levelMap = levelMap.map { (level, maze) -> level to maze.expandMaze(levelMap, level) }
      .toMap().toMutableMap().withDefault { EMPTY_MAZE }
    // check the outer edge
    when {
      levelMap.getValue(currentMaxLevel).filter { it.key != CENTER }.any { it.value == BUG } -> levelMap[++currentMaxLevel] =
        emptyMaze()
    }
    when {
      levelMap.getValue(currentMinLevel).filter { it.key != CENTER }.any { it.value == BUG } -> levelMap[--currentMinLevel] =
        emptyMaze()
    }
    time++
//    println("-------------SUMARY---------------")
//    println("Iteration $time levels: ($currentMinLevel, $currentMaxLevel)")
//    println("Bugs: ${levelMap.map {level -> level.value.count { it.value == BUG } }.sum()}")
//    levelMap.forEach { (level, maze) ->
//      println("Level $level\n")
//      maze.render()
//
//    }
  }
  println("Bugs: ${levelMap.map { level -> level.value.count { it.value == BUG } }.sum()}")
}

fun emptyMaze(): ErisMaze =
  (0 until 5).flatMap { y -> (0 until 5).map { x -> (Coordinate(x, y) to EMPTY) } }.toMap().withDefault { EMPTY }


fun ErisMaze.expandMaze(levelMap: LevelMap, level: Int) =
  keys.filter { it != CENTER }.fold(mutableMapOf<Coordinate, ErisTile>()) { acc, coordinate ->
    val adjacentBugs = coordinate.adjacentCoordinatesRecursive(level)
      .map { (level, coordinate) -> levelMap.getValue(level).getValue(coordinate) }.count { it == BUG }
    acc.also {
      acc[coordinate] = when (getValue(coordinate)) {
        BUG -> when (adjacentBugs) {
          1 -> BUG
          else -> EMPTY
        }
        EMPTY -> when (adjacentBugs) {
          1, 2 -> BUG
          else -> EMPTY
        }
      }
    }
  }.toMap().withDefault { EMPTY }

fun Map<Coordinate, ErisTile>.render() {
  when {
    isEmpty() -> println("Empty maze")
    else -> {
      val (xMin, xMax) = xRange()
      val (yMin, yMax) = yRange()
      for (y in yMin..yMax) {
        for (x in xMin..xMax) {
          when {
            x == 2 && y == 2 -> print('?')
            else -> print(getValue(Coordinate(x, y)).char)
          }
        }
        println()
      }
    }
  }
}

private fun Coordinate.adjacentCoordinatesRecursive(currentLevel: Int) = adjacentCoordinates(this).map {
  when {
    it == CENTER -> when (this) {
      Coordinate(1, 2) -> (0 until 5).map { x -> currentLevel + 1 to Coordinate(0, x) }
      Coordinate(3, 2) -> (0 until 5).map { x -> currentLevel + 1 to Coordinate(4, x) }
      Coordinate(2, 1) -> (0 until 5).map { y -> currentLevel + 1 to Coordinate(y, 0) }
      Coordinate(2, 3) -> (0 until 5).map { y -> currentLevel + 1 to Coordinate(y, 4) }
      else -> error("Unknown adjacent coordinate encounterd $this")
    }
    it.x == -1 -> listOf(currentLevel - 1 to Coordinate(1, 2))
    it.x == 5 -> listOf(currentLevel - 1 to Coordinate(3, 2))
    it.y == -1 -> listOf(currentLevel - 1 to Coordinate(2, 1))
    it.y == 5 -> listOf(currentLevel - 1 to Coordinate(2, 3))
    else -> listOf(currentLevel to it)
  }
}.flatten()
