package day7.second

import day7.first.generatePermutations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tools.executeAsyncProgram
import tools.readInput
import tools.timeSolution

fun main() = timeSolution {
  val code = readInput()
  val permutations = generatePermutations(mutableListOf(5, 6, 7, 8, 9))
  val result = permutations.map {
    runBlocking {
      val queue1 = Channel<Int>(5)
      val queue2 = Channel<Int>(5)
      val queue3 = Channel<Int>(5)
      val queue4 = Channel<Int>(5)
      val queue5 = Channel<Int>(5)
      queue1.send(it[1])
      queue2.send(it[2])
      queue3.send(it[3])
      queue4.send(it[4])
      queue5.send(it[0])
      queue5.send(0)
      async { executeAsyncProgram(code, queue5, queue1) }.start()
      async { executeAsyncProgram(code, queue1, queue2) }.start()
      async { executeAsyncProgram(code, queue2, queue3) }.start()
      async { executeAsyncProgram(code, queue3, queue4) }.start()
      withContext(Dispatchers.Default) {
        executeAsyncProgram(code.copyOf(), queue4, queue5)
      }
      Pair(it.joinToString(","), queue5.receive())
    }
  }.maxBy(Pair<String, Int>::second)
  println(result)
}

