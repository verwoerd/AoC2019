package day6.first


import tools.timeSolution

fun main() = timeSolution {
  val pointsMap = readInput()
  println(pointsMap.keys.map { pointsMap.calculateOrbitsFromOrigin(it) }.sum())
}

fun readInput(): MutableMap<String, MutableList<String>> =
  System.`in`.bufferedReader().readLines().map(::parseOrbitLine).fold(mutableMapOf()) { map, (parent, child) ->
    map.putIfAbsent(parent, mutableListOf())
    map.putIfAbsent(child, mutableListOf())
    map[child]!! += parent
    map
  }

fun parseOrbitLine(line: String) = line.split(")")

fun Map<String, MutableList<String>>.calculateOrbitsFromOrigin(key: String): Int {
  var current = key
  var count = 0
  while ((this[current] ?: emptyList<String>()).isNotEmpty()) {
    current = this[current]?.first() ?: error("")
    count++
  }
  return count
}
