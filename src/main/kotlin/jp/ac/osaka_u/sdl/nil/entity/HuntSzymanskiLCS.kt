package jp.ac.osaka_u.sdl.nil.entity

/**
 * Time complexity is O(N log (N)).
 * https://dl.acm.org/doi/10.1145/359581.359603
 */
class HuntSzymanskiLCS : LCS {
    override fun calcLength(a: TokenSequence, b: TokenSequence): Int {
        val (shorter: TokenSequence, longer: TokenSequence) =
            if (a.size < b.size) {
                a to b
            } else {
                b to a
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
        return lcs.binarySearch(Int.MAX_VALUE - 1).inv() - 1
    }
}
