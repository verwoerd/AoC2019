package day4.first

import tools.timeSolution

fun main() = timeSolution {
  println(isValidPassword(111111))
  println(isValidPassword(223450))
  println(isValidPassword(123789))
  val (min, max) = readLine()!!.split("-").map { it.toInt() }
  val valid = mutableSetOf<Int>()
  for (i in min..max) {
    if (isValidPassword(i)){valid.add(i)}
  }
  println("Found possbile passwords in range: ${valid.size}")
}

fun isValidPassword(number: Int): Boolean = neverDecreases(number) && atleastOneDoubleSequential(number)

fun neverDecreases(number: Int) = (number / 100_000 % 10 <= number / 10_000 % 10) &&
    (number / 10_000 % 10 <= number / 1000 % 10) &&
    (number / 1000 % 10 <= number / 100 % 10) &&
    (number / 100 % 10 <= number / 10 % 10) &&
    (number / 10 % 10 <= number % 10)

fun atleastOneDoubleSequential(number: Int) =(number / 100_000 % 10 == number / 10_000 % 10) ||
    (number / 10_000 % 10 == number / 1000 % 10) ||
    (number / 1000 % 10 == number / 100 % 10) ||
    (number / 100 % 10 == number / 10 % 10) ||
    (number / 10 % 10 == number % 10)




