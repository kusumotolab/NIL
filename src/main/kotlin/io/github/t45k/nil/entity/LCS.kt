package io.github.t45k.nil.entity

interface LCS {
    /**
     * Calculate the length of LCS between given token sequence a and b.
     */
    fun calcLength(a: TokenSequence, b: TokenSequence): Int
}
