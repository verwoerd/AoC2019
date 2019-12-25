package day25.first

import day15.first.Direction
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tools.sendCharCommands
import tools.startComputerWithCustomOutputAsync
import tools.timeSolution
import java.util.Stack

fun main() = timeSolution {
  runBlocking {
    startComputerWithCustomOutputAsync { _, input, output ->
      launch { locationParser(output, input) }
    }.join()
  }
}


val forbiddenItems = setOf("molten lava", "escape pod", "infinite loop", "photons", "giant electromagnet")

suspend fun locationParser(
  channel: ReceiveChannel<Long>,
  input: SendChannel<Long>
                          ) {
  var rooms = 0
  val stack = Stack<Direction>()
  val items = mutableSetOf<String>()
  val seenRooms = mutableSetOf<String>()
  val pathToCheckPoint = mutableListOf<Direction>()
  var checkpointFound = false
  val roomsDescriptions = mutableMapOf<String, GameOutput>()
  while (!channel.isClosedForReceive) {
    val output = printOutput(channel)
    val result = roomRegex.find(output)?.groups ?: error("Invalid regex")
    val currentRoom = GameOutput(
      name = result[1]!!.value,
      description = result[2]!!.value,
      item = parseItems(result[6]?.value ?: ""),
      doors = parseDoors(result[3]!!.value)
                                )
    if (seenRooms.add(currentRoom.name)) {
      currentRoom.item.filter { it !in forbiddenItems }.forEach {
        sendCharCommands("take $it", input)
        items.add(it)
        silentOutput(channel)
      }
      when (currentRoom.name) {
        "Security Checkpoint" -> {
          // we deal with this later, finish exploration
          checkpointFound = true
        }
        else -> {
          currentRoom.doors.forEach {
            stack.add(it.returnDirection())
            stack.add(it)
          }
        }
      }
      rooms++
    }

    if (stack.isEmpty()) break
    val nextCommand = stack.pop()
    roomsDescriptions.putIfAbsent(currentRoom.name, currentRoom)
    if (!checkpointFound) {
      pathToCheckPoint.add(nextCommand)
    }
    sendCharCommands(nextCommand.name.toLowerCase(), input)
  }

  // lets go to the checkpoint
  pathToCheckPoint.forEach {
    sendCharCommands(it.name.toLowerCase(), input)
    silentOutput(channel)
  }

  // calculate all possible combinations
  val combinations = allCombinations(items).iterator()
  // drop all items on the floor
  items.forEach {
    sendCharCommands("drop $it", input)
    silentOutput(channel)
  }
  var finalCombination: Set<String> = emptySet()
  checkpoint@ for (currentCombination in combinations) {
    // take the combination
    currentCombination.forEach {
      sendCharCommands("take $it", input)
      silentOutput(channel)
    }
    // visit the room
    sendCharCommands("north", input)
    val output = printOutput(channel)
    when {
      "Alert! Droids on this ship are heavier than the detected value!" !in output &&
          "Alert! Droids on this ship are lighter than the detected value!" !in output -> {
        finalCombination = currentCombination
        break@checkpoint
      }
    }
    // drop all items
    currentCombination.forEach {
      sendCharCommands("drop $it", input)
      silentOutput(channel)
    }
  }
  rooms++
  println("==========================================================")
  println("Visited $rooms rooms")
  println("Found items: $items")
  println("Path to CheckPoint(very likely a detour): $pathToCheckPoint")
  println("Found combination for weight: $finalCombination")
  println("Rooms:")
  println(roomsDescriptions.map { "${it.key}: ${it.value.description}" }.joinToString("\n"))
  println("==========================================================")


}

fun parseDoors(input: String): MutableList<Direction> {
  val result = mutableListOf<Direction>()
  if ("east" in input) result.add(Direction.EAST)
  if ("west" in input) result.add(Direction.WEST)
  if ("north" in input) result.add(Direction.NORTH)
  if ("south" in input) result.add(Direction.SOUTH)
  return result
}

fun parseItems(input: String) = input.split('\n').map { it.removePrefix("- ").trim() }.filter { it.isNotBlank() }


data class GameOutput(
  val name: String,
  val description: String,
  val doors: List<Direction>,
  val item: List<String>
                     )

val roomRegex = Regex(
  "\n\n\n== (?<name>.*) ==\n(?<description>.*)\n\nDoors here lead:\n((- .*\\n)+)\n(Items here:\n((- .*\\n)+)\n)?Command\\?\n",
  RegexOption.MULTILINE
                     )

suspend fun printOutput(channel: ReceiveChannel<Long>): String {
  var output = ""
  while (!output.endsWith("Command?\n") && !channel.isClosedForReceive) {
    val char = channel.receive().toChar()
    print(char)
    output += char
  }
  return output
}

suspend fun silentOutput(channel: ReceiveChannel<Long>): String {
  var output = ""
  while (!output.endsWith("Command?\n") && !channel.isClosedForReceive) {
    val char = channel.receive().toChar()
    output += char
  }
  return output
}


fun allCombinations(fullSet: Set<String>): Set<Set<String>> {
  if (fullSet.isEmpty()) return emptySet()
  return fullSet.flatMap { string ->
    val next = fullSet.toMutableSet().also { it.remove(string) }.toSet()
    mutableSetOf(next).also { it.addAll(allCombinations(next)) }
  }.toSet()
}
