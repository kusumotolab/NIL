package io.github.t45k.nil.entity

typealias TokenSequence = List<Int>
typealias NGrams = List<Int>

fun TokenSequence.toNgrams(gramSize: Int): NGrams =
    (0..(this.size - gramSize))
        .map { this.subList(it, it + gramSize).hashCode() }
        .distinct()
