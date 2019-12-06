package day6.second

import day6.first.readInput
import tools.timeSolution

fun main() = timeSolution {
  val pointsMap = readInput()
  val pathOfSanta = pointsMap.calculateOrbitPathToCenterOfMass("SAN")
  val myPath = pointsMap.calculateOrbitPathToCenterOfMass("YOU")
  val sharedPath = pathOfSanta.intersect(myPath)
  println(pathOfSanta.size + myPath.size - 2 * sharedPath.size)
}

fun Map<String, MutableList<String>>.calculateOrbitPathToCenterOfMass(key: String): List<String> {
  var current = this[key]?.first() ?: error("")
  val path = mutableListOf<String>()
  while ((this[current] ?: emptyList<String>()).isNotEmpty()) {
    path.add(current)
    current = this[current]?.first() ?: error("")
  }
  path.add(current)
  return path
}
