package tools

import java.util.PriorityQueue
import kotlin.math.ceil

/**
 * @author verwoerd
 * @since 5-12-2019
 */
fun boolToInt(expression: Boolean) = if (expression) 1 else 0

fun boolToLong(expression: Boolean) = if (expression) 1L else 0L
infix fun Long.ceilDivision(b: Number) = ceil(this / b.toDouble()).toLong()
infix fun Int.ceilDivision(b: Number) = ceil(this / b.toDouble()).toInt()

fun <T> priorityQueueOf(vararg args: T): PriorityQueue<T> {
  val queue = PriorityQueue<T>()
  queue.addAll(args)
  return queue
}

fun <T> priorityQueueOf(comparator: Comparator<T>, vararg args: T): PriorityQueue<T> {
  val queue = PriorityQueue<T>(comparator)
  queue.addAll(args)
  return queue
}
