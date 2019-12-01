package day1.second

import tools.timeSolution

fun main() = timeSolution {
  println(System.`in`.bufferedReader().lines().map { calculateModuleFuel(it.toLong()) }.reduce(Long::plus).get())
}

fun calculateFuel(weight: Long): Long = weight / 3 - 2

fun calculateModuleFuel(startFuel: Long): Long = when (val fuel = calculateFuel(startFuel)) {
  in 0..8 -> fuel
  else -> fuel + calculateModuleFuel(fuel)
}
