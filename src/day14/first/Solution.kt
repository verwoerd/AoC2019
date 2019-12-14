package day14.first

import tools.ceilDivision
import tools.timeSolution
import java.util.LinkedList
import kotlin.math.max

fun main() = timeSolution {
  val reactionList = readReactionInput()
  println(solver(reactionList, 1L))
}

fun readReactionInput() = System.`in`.bufferedReader().readLines()
  .map { it.split("=>") }
  .map { (input, output) ->
    val (factor, result) = parseReaction(output)
    val inputs = input.split(",").map(::parseReaction).map { (amount, result) -> result to amount }
    result to Reaction(result, factor, inputs)
  }.toMap()


val reactionRegex = Regex("(\\d+) (\\w+)")
fun parseReaction(reaction: String) = reactionRegex.matchEntire(reaction.trim())
  .let { it!!.groupValues[1].toInt() to it.groupValues[2] }

data class Reaction(val result: String, val produces: Int, val ingredients: List<Pair<String, Int>>) {
  fun isBaseElement() =
    ingredients.size == 1 && ingredients.first().first == "ORE"

  fun oreCost() =
    ingredients.first().second
}

fun solver(reactionList: Map<String, Reaction>, fuel: Long): Long {
  val currentNeeds = mutableMapOf("FUEL" to fuel).withDefault { 0 }
  val currentRemains = mutableMapOf<String, Long>().withDefault { 0 }
  val queue = LinkedList<Pair<String, Long>>()
  queue.add("FUEL" to fuel)
  while (queue.isNotEmpty()) {
    val (current, need) = queue.poll()
    val reaction = reactionList.getValue(current)
    val numReactions = need ceilDivision reaction.produces
    when {
      reaction.isBaseElement() -> {
        currentNeeds["ORE"] = currentNeeds.getValue("ORE") + numReactions * reaction.oreCost()
      }
      else -> {
        reaction.ingredients.forEach { (input, amount) ->
          val currentReaction = reactionList.getValue(input)
          val needToProduce = max(numReactions * amount - currentRemains.getValue(input), 0L)
          val numResult = needToProduce ceilDivision currentReaction.produces
          currentNeeds[input] = currentNeeds.getValue(input) + need * amount
          currentRemains[input] =
            (numResult * currentReaction.produces) + currentRemains.getValue(input) - numReactions * amount
          if (needToProduce > 0) {
            queue.add(input to (needToProduce))
          }
        }
      }
    }
  }
  return currentNeeds.getValue("ORE")
}
