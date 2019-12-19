package day19.first

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tools.executeBigProgramAsync
import tools.loadLongAsyncCode
import tools.timeSolution

fun main() = timeSolution {
  val code = loadLongAsyncCode()
  var result = 0L
  runBlocking {
    val input = Channel<Long>()
    val output = Channel<Long>(Channel.UNLIMITED)
    for (y in 0L..49L) {
      for (x in 0L..49L) {
        launch {
          executeBigProgramAsync(code, input, output)
        }
        input.send(x)
        input.send(y)
        val outputValue = output.receive()
        when (outputValue) {
          0L -> print('.')
          else -> print('#')
        }
        result += outputValue
      }
      println()
    }
  }
  println(result)
}

