package day1.first

import tools.timeSolution

fun main() = timeSolution {
  println(System.`in`.bufferedReader().lines().map { it.toLong() / 3 - 2 }.reduce(Long::plus).get())
}
