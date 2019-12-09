package day9.second

import kotlinx.coroutines.runBlocking
import tools.executeBigProgram
import tools.timeSolution

fun main() = timeSolution {
  val code = readLine()!!.split(",").mapIndexed { index, it -> index.toLong() to it.toLong() }.toMap()
  runBlocking {
    executeBigProgram(code, { 2 }, ::println)
  }
}
