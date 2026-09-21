<?php
function main1() {
    // Comment
    for ($i = 1; $i <= 100; $i++) {
        if ($i % 15 == 0) {
            echo "FizzBuzz";
        } elseif ($i % 3 == 0) {
            echo "Fizz";
        } elseif ($i % 5 == 0) {
            echo "Buzz";
        } else {
            echo $i;
        }
    }
}

function main2() {
    for ($i = 1; $i <= 100; $i++) {
        // Comment
        if ($i % 15 == 0) {
            echo "FizzBuzz";
        } elseif ($i % 3 == 0) {
            echo "Fizz";
        } elseif ($i % 5 == 0) {
            echo "Buzz";
        } else {
            echo $i;
        }
    }
}
