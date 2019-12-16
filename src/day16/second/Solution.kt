package day16.second

import tools.timeSolution
import kotlin.math.abs
import kotlin.math.min

fun main() = timeSolution {
  val input = readLine()!!.toCharArray().map { (it - '0').toLong() }
  val offset = input.take(7).joinToString("").toInt()
  val work = generateInput(input).drop(offset).take(10_000 * input.size - offset).toList()
  val results = fft(work, offset)
  println(results.take(8).joinToString(""))
}

fun generateInput(input: List<Long>) = sequence {
  var index = -1
  while (true)
    yield(input[++index % input.size])
}

// Observation, numbers are only depend on number following them, so no point calculating the ones before them
// we can re-use partial sums
// Note this code only works for offsets in the second half of the list
fun fft(input: List<Long>, position: Int): List<Long> {
  var result: List<Long> = input.toCollection(mutableListOf())
  var phase = 0
  while (phase < 100) {
    var start = 0
    var step = position
    var sum = 0L
    var sign = 1L
    while (start < result.size) {
      sum += result.subList(start, min(start + step, result.size)).sum() * sign
      start += 2 * position
      sign *= -1
    }
    result = result.indices.map { index ->
      if (index != 0) {
        // Does not deal with the minus part for the first half of the list
        sum -= result[index - 1]
        sum += result.subList(min(result.size, step), min(result.size, index + step)).sum()
      }
      step++
      abs(sum) % 10
    }
    phase++
  }
  return result
}

