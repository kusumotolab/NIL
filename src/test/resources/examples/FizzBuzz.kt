fun main1() {
    // Comment
    for (number in 1..100) {
        println(
            when {
                number % 15 == 0 -> "FizzBuzz"
                number % 3 == 0 -> "Fizz"
                number % 5 == 0 -> "Buzz"
                else -> number
            }
        )
    }
}

fun main2() {
    /*
    Comments
     */
    for (number in 1..100) {
        println(
            when {
                number % 15 == 0 -> "FizzBuzz"
                number % 3 == 0 -> "Fizz"
                number % 5 == 0 -> "Buzz"
                else -> number
            }
        )
    }
}
