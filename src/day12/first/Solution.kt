package day12.first

import tools.timeSolution
import java.util.stream.Collectors
import kotlin.math.abs

const val STEPS = 1000

fun main() = timeSolution {
  var scan = readPlanetCoordinates()
  for (step in 1..STEPS) {
    scan = scan.map { (location, velocity) ->
      val newVelocity = scan.fold(velocity) { (x, y, z), (nextLocation) ->
        PlanetCoordinate(
          x + applyGravity(location.x, nextLocation.x),
          y + applyGravity(location.y, nextLocation.y),
          z + applyGravity(location.z, nextLocation.z)
                        )
      }
      applyVelocity(location, newVelocity) to newVelocity
    }.toMutableList()
//    scan.printReport(step)
  }
  println(scan.map { (location, velocity) -> location.potentialEnergy() * velocity.potentialEnergy() }.sum())
}

data class PlanetCoordinate(var x: Int, var y: Int, var z: Int) {
  fun potentialEnergy() = abs(x) + abs(y) + abs(z)
}


val regex = Regex("<x=(-?\\w+), y=(-?\\w+), z=(-?\\w+)>")
fun readPlanetCoordinates(): MutableList<Pair<PlanetCoordinate, PlanetCoordinate>> =
  System.`in`.bufferedReader().lines().map { regex.find(it)!!.groups }.map {
    PlanetCoordinate(
      it[1]!!.value.toInt(),
      it[2]!!.value.toInt(),
      it[3]!!.value.toInt()
                    ) to PlanetCoordinate(0, 0, 0)
  }.collect(Collectors.toList())

fun applyGravity(base: Int, other: Int) = when {
  other > base -> 1
  other == base -> 0
  other < base -> -1
  else -> throw IllegalArgumentException("$base $other")
}

fun applyVelocity(location: PlanetCoordinate, velocity: PlanetCoordinate) =
  PlanetCoordinate(location.x + velocity.x, location.y + velocity.y, location.z + velocity.z)

fun MutableList<Pair<PlanetCoordinate, PlanetCoordinate>>.printReport(step: Number) {
  println("after $step steps")
  this.forEach { (location, velocity) -> println("pos=<x=${location.x}, y=${location.y}, z=${location.z}>, vel=<x=${velocity.x}, y=${velocity.y}, z=${velocity.z}>") }
}
