package day9.first

import kotlinx.coroutines.runBlocking
import tools.executeBigProgram
import tools.timeSolution

fun main() = timeSolution {
  val code = readLine()!!.split(",").mapIndexed { index,it -> index.toLong() to it.toLong() }.toMap()
  runBlocking {
    executeBigProgram(code, { 1 }, ::println)
  }
}
