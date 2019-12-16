package day16.first


import tools.timeSolution
import kotlin.math.abs


fun main() = timeSolution {
  var input = readLine()!!.toCharArray().map { (it - '0').toLong() }
  val patterns = patternGenerator(input.size).take(input.size).toList()
  patterns.map { it.joinToString(",") }.forEach(::println)
  input = fft(input, patterns)
  println(input.take(8).joinToString(""))

}

val PATTERN = listOf(0L, 1L, 0L, -1L)

fun patternGenerator(inputSize: Int) = sequence {
  var currentPattern: LongArray
  var iteration = 1
  while (true) {
    currentPattern = LongArray(inputSize) { PATTERN[((it + 1) / iteration) % (PATTERN.size)] }
    yield(currentPattern.toList())
    iteration++
  }
}

fun fft(input: List<Long>, patterns: List<List<Long>>): List<Long> {
  var result: List<Long> = input.toCollection(mutableListOf())
  var phase = 0
  while (phase < 100) {
    result = patterns.map {
      abs(it.mapIndexed { index, value -> value * result[index] }.sum()) % 10L
    }
    phase++
    println("after phase $phase: ${result.joinToString("")}")
  }
  return result
}
