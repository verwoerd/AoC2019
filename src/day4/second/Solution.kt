package day4.second

import tools.timeSolution
// BLEH, I tried to solve the wrong problem where the grouped digits may be part of a group of larger then 2
fun main() = timeSolution {
  println("should all be false")
  println(isValidPassword(123444))
  println(isValidPassword(277777))
  println(isValidPassword(777778))
  println(isValidPassword(223450))
  println(isValidPassword(123789))
  println(isValidPassword(444555))
  println("xxxxxx" + isValidPassword(222222)) //xxxxxx
  println("xxxx89" + isValidPassword(222289))
  println("0xxxx9" + isValidPassword(233338))
  println("01xxxx" + isValidPassword(234444))

  println("should all be true")
  println(isValidPassword(111223))
  println(isValidPassword(111122))
  println(isValidPassword(112233))
  println(isValidPassword(277888))
  println("xxxxyy" + isValidPassword(222288)) //xxxxyy
  println("xxyyyy" + isValidPassword(228888)) //xxyyyy
  println("xxyy78" + isValidPassword(223389))
  println("xxyyzz" + isValidPassword(227788)) //
  println("0xxyy9" + isValidPassword(233448))
  println("1xx678" + isValidPassword(122678))
  println("1xx6yy" + isValidPassword(122688))
  println("01xxyy" + isValidPassword(234455))
  println("012xx9" + isValidPassword(234559))
  println("0123xx" + isValidPassword(234588))
  println("xx56yy" + isValidPassword(226788))
  println("xx5yy9" + isValidPassword(225889))
  println("xx5678" + isValidPassword(225678))

  val (min, max) = readLine()!!.split("-").map { it.toInt() }
  val valid = mutableSetOf<Int>()
  for (i in min..max) {
    if (isValidPassword(i)) {
      valid.add(i)
    }
  }
  println("Found possible passwords in range: ${valid.size}")
}

fun isValidPassword(number: Int): Boolean =
  neverDecreases(number) && atleastOneDoubleSequential(number)
 // && !groupsOfFive(number) && !groupsOfThree(number)

fun neverDecreases(number: Int) = (number / 100_000 % 10 <= number / 10_000 % 10) &&
    (number / 10_000 % 10 <= number / 1000 % 10) &&
    (number / 1000 % 10 <= number / 100 % 10) &&
    (number / 100 % 10 <= number / 10 % 10) &&
    (number / 10 % 10 <= number % 10)

fun atleastOneDoubleSequential(number: Int) =
  (number / 100_000 % 10 == number / 10_000 % 10 && number / 10_000 % 10 != number / 1000 % 10) ||
    (number / 100_000 % 10 != number / 10_000 % 10 && number / 10_000 % 10 == number / 1000 % 10 && number / 1000 % 10 != number / 100 % 10) ||
    (number / 10_000 % 10 != number / 1000 % 10 && number / 1000 % 10 == number / 100 % 10 && number / 100 % 10 != number / 10 % 10) ||
    (number / 1000 % 10 != number / 100 % 10 && number / 100 % 10 == number / 10 % 10 && number / 10 % 10 != number % 10) ||
    (number / 100 % 10 != number / 10 % 10 && number / 10 % 10 == number % 10)

// NOTE TO SELF: READ THE PROBLEM SPECIFICATION CAREFULLY!
fun groupsOfFive(number: Int) =
  (number / 100_000 % 10 == number / 10_000 % 10 && number / 10_000 % 10 == number / 1000 % 10 && number / 1000 % 10 == number / 100 % 10 && number / 100 % 10 == number / 10 % 10 && number / 10 % 10 != number % 10) ||
      (number / 100_000 % 10 != number / 10_000 % 10 && number / 10_000 % 10 == number / 1000 % 10 && number / 1000 % 10 == number / 100 % 10 && number / 100 % 10 == number / 10 % 10 && number / 10 % 10 == number % 10)

fun groupsOfThree(number: Int) =
  (number / 100_000 % 10 == number / 10_000 % 10 && number / 10_000 % 10 == number / 1000 % 10 && number / 1000 % 10 != number / 100 % 10) ||
      (number / 100_000 % 10 != number / 10_000 % 10 && number / 10_000 % 10 == number / 1000 % 10 && number / 1000 % 10 == number / 100 % 10 && number / 100 % 10 != number / 10 % 10) ||
      (number / 10_000 % 10 != number / 1000 % 10 && number / 1000 % 10 == number / 100 % 10 && number / 100 % 10 == number / 10 % 10 && number / 10 % 10 != number % 10) ||
      (number / 1000 % 10 != number / 100 % 10 && number / 100 % 10 == number / 10 % 10 && number / 10 % 10 == number % 10)


