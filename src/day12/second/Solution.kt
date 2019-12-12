package day12.second

import day12.first.applyGravity
import day12.first.readPlanetCoordinates
import tools.timeSolution

fun main() = timeSolution {
  val scan = readPlanetCoordinates().map { it.first }

  val xSteps = calculateLoop(scan.map { it.x })
  val ySteps = calculateLoop(scan.map { it.y })
  val zSteps = calculateLoop(scan.map { it.z })

  println("Found orbits are repeating for scan x=$xSteps y=$ySteps z=$zSteps")

  println(lcm(xSteps, lcm(ySteps, zSteps)))
}

fun calculateLoop(startLocation: List<Int>) =
  generateSequence(Triple(0L, startLocation.toList(), listOf(0, 0, 0, 0))) { (steps, locations, velocity) ->
    val nextVelocity = velocity.zip(locations.map { location ->
      locations.map { applyGravity(location, it) }.sum()
    }, Int::plus)
    val nextLocations = locations.zip(nextVelocity, Int::plus)
    Triple(steps + 1, nextLocations, nextVelocity)
  }.drop(1).first { (_, locations, velocity) -> velocity.all { it == 0 } && locations == startLocation }.first

fun lcm(a: Long, b: Long) = (a * b) / gcd(a, b)

fun gcd(a: Long, b: Long): Long = when (b) {
  0L -> a
  else -> gcd(b, a % b)
}
