package jp.ac.osaka_u.sdl.nil.entity

import kotlin.math.max

@Deprecated("Time Complexity of naive LCS is O(NM) where N and M are size of given two sequence respectively.\nIt is too late")
class NaiveLCS : LCS {
    override fun calcLength(a: TokenSequence, b: TokenSequence): Int {
        val size1 = a.size
        val size2 = b.size
        val dpTable: Array<Array<Int>> = Array(size1 + 1) { Array(size2 + 1) { 0 } }
        for (i in 1..size1) {
            for (j in 1..size2) {
                if (a[i - 1] == b[j - 1]) {
                    dpTable[i][j] = dpTable[i - 1][j - 1] + 1
                } else {
                    dpTable[i][j] = max(dpTable[i - 1][j], dpTable[i][j - 1])
                }
            }
        }

        return dpTable[size1][size2]
    }
}
