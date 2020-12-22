package io.github.t45k.nil.core

import io.github.t45k.nil.NILConfig
import io.github.t45k.nil.entity.TokenSequence
import java.util.Comparator
import kotlin.math.max
import kotlin.math.min

class Verification(private val config: NILConfig) {

    /**
     * Verification is based on Hunt-Szymanski algorithm
     */
    fun verify(tokenSequence1: TokenSequence, tokenSequence2: TokenSequence): Boolean {
        val (shorter: TokenSequence, longer: TokenSequence) =
            if (tokenSequence1.size < tokenSequence2.size) {
                tokenSequence1 to tokenSequence2
            } else {
                tokenSequence2 to tokenSequence1
            }
        val (n, m) = shorter.size to longer.size

        val invertedIndices: MutableMap<Int, MutableList<Int>> = mutableMapOf()
        for (i in m - 1 downTo 0) {
            invertedIndices.getOrPut(longer[i]) { mutableListOf() }.add(i)
        }

        val lcs = Array(n + 1) { Int.MAX_VALUE }.also { it[0] = -1 }
        val lowerBoundComparator = Comparator { x: Int, y: Int -> if (x >= y) 1 else -1 }
        for (value: Int in shorter) {
            if (!invertedIndices.containsKey(value)) {
                continue
            }
            for (indexOfB in invertedIndices[value]!!) {
                val index: Int = lcs.binarySearch(indexOfB, lowerBoundComparator).inv()
                lcs[index] = indexOfB
            }
        }
        return (lcs.binarySearch(Int.MAX_VALUE - 1).inv() - 1) * 100 / n >= config.verifyingThreshold
    }

    @Deprecated("Time Complexity of naive LCS is O(NM) where N and M are size of given two sequence respectively.\nIt is too late")
    fun verifyAlternative(tokenSequence1: TokenSequence, tokenSequence2: TokenSequence): Boolean {
        val size1 = tokenSequence1.size
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
        return dpTable[size1][size2] * 100 / min >= config.verifyingThreshold
    }
}
