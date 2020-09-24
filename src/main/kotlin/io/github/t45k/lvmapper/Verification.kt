package io.github.t45k.lvmapper

import io.github.t45k.lvmapper.entity.CodeBlock
import kotlin.math.max
import kotlin.math.min

class Verification(private val codeBlocks: List<CodeBlock>) {

    /*
    TODO use faster LCS
     */
    fun verify(id1: Int, id2: Int): Boolean {
        val tokenSequence1 = codeBlocks[id1].tokenSequence
        val size1 = tokenSequence1.size
        val tokenSequence2 = codeBlocks[id2].tokenSequence
        val size2 = tokenSequence2.size
        val dpTable: Array<Array<Int>> = Array(size1 + 1) { Array(size2 + 1) { 0 } }
        for (i in 1..size1) {
            for (j in 1..size2) {
                if (tokenSequence1[i - 1] == tokenSequence2[j - 1]) {
                    dpTable[i][j] = dpTable[i - 1][j - 1] + 1
                } else {
                    dpTable[i][j] = max(dpTable[i - 1][j], dpTable[i][j - 1])
                }
            }
        }

        val min = min(size1, size2)
        return dpTable[size1][size2] * 100 / min >= calcVerifyingThreshold(min)
    }

    private fun calcVerifyingThreshold(size: Int) =
        when {
            size <= 10 -> 70
            size >= 20 -> 40
            else -> -3 * size + 100
        }
}
