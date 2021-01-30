package jp.ac.osaka_u.sdl.nil.entity

/**
 * TokenSequence is a list of each token's hash value.
 */
typealias TokenSequence = List<Int>

fun TokenSequence.toNgrams(gramSize: Int): NGrams =
    (0..(this.size - gramSize))
        .map { this.subList(it, it + gramSize).hashCode() }
        .distinct()
