package jp.ac.osaka_u.sdl.nil.entity

interface LCS {
    /**
     * Calculate the length of LCS between given token sequence a and b.
     */
    fun calcLength(a: TokenSequence, b: TokenSequence): Int
}
