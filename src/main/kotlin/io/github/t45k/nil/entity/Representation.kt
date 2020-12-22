package io.github.t45k.nil.entity

/**
 * TokenSequence is a list of each token's hash value.
 */
typealias TokenSequence = List<Int>

/**
 * NGrams is a list of each N-gram's hash value.
 */
typealias NGrams = List<Int>

fun TokenSequence.toNgrams(gramSize: Int): NGrams =
    (0..(this.size - gramSize))
        .map { this.subList(it, it + gramSize).hashCode() }
        .distinct()
