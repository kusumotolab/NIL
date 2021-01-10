package io.github.t45k.nil.usecase

import io.github.t45k.nil.entity.LCS
import io.github.t45k.nil.entity.TokenSequence
import kotlin.math.min

class Verification(private val lcs: LCS, private val threshold: Int) {

    fun verify(tokenSequence1: TokenSequence, tokenSequence2: TokenSequence): Boolean {
        val min = min(tokenSequence1.size, tokenSequence2.size)
        return lcs.calcLength(tokenSequence1, tokenSequence2) * 100 / min >= threshold
    }
}
