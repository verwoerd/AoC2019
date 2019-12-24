package day24.first

import day24.first.ErisTile.BUG
import day24.first.ErisTile.EMPTY
import tools.Coordinate
import tools.adjacentCoordinates
import tools.timeSolution
import java.math.BigInteger


fun main() = timeSolution {
  var maze = System.`in`.bufferedReader().readLines()
    .mapIndexed { y, line -> line.toCharArray().mapIndexed { x, char -> Coordinate(x, y) to char.toTile() } }.flatten()
    .toMap().withDefault { EMPTY }
  val seen = mutableSetOf<Map<Coordinate, ErisTile>>()

  while (seen.add(maze)) {
    maze = maze.keys.fold(mutableMapOf<Coordinate, ErisTile>()) { acc, coordinate ->
      val adjacentBugs = adjacentCoordinates(coordinate).map { maze.getValue(it) }.count { it == BUG }
      acc.also {
        acc[coordinate] = when (maze.getValue(coordinate)) {
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
    }.withDefault { EMPTY }
    println(
      maze.keys.filter { maze.getValue(it) == BUG }.map { 2.toBigInteger().pow(it.x + 5 * it.y) }.reduce(
        BigInteger::add
                                                                                                        )
           )
  }

}

enum class ErisTile(val char: Char) {
  BUG('#'), EMPTY('.')
}

fun Char.toTile() = when (this) {
  BUG.char -> BUG
  EMPTY.char -> EMPTY
  else -> error("Invalid tile encountered")
}
