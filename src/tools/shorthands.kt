package tools

import kotlin.math.ceil

/**
 * @author verwoerd
 * @since 5-12-2019
 */
fun boolToInt(expression: Boolean) = if (expression) 1 else 0

fun boolToLong(expression: Boolean) = if (expression) 1L else 0L
infix fun Long.ceilDivision(b: Number) = ceil(this / b.toDouble()).toLong()
