package main

func main1() {
    // Comment
    for i := 1; i <= 100; i++ {
        if i%15 == 0 {
            println("FizzBuzz")
        } else if i%3 == 0 {
            println("Fizz")
        } else if i%5 == 0 {
            println("Buzz")
        } else {
            println(i)
        }
    }
}

func main2() {
    for i := 1; i <= 100; i++ {
        // Comment
        if i%15 == 0 {
            println("FizzBuzz")
        } else if i%3 == 0 {
            println("Fizz")
        } else if i%5 == 0 {
            println("Buzz")
        } else {
            println(i)
        }
    }
}
