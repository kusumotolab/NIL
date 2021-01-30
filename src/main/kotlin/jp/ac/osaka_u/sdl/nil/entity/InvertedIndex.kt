package jp.ac.osaka_u.sdl.nil.entity

import kotlin.math.min

class InvertedIndex private constructor() {
    companion object {
        /**
         * Alternative constructor
         */
        fun create(
            partitionSize: Int,
            gramSize: Int,
            tokenSequences: List<TokenSequence>,
            startIndex: Int
        ): InvertedIndex {
            val invertedIndex = InvertedIndex()
            val endIndex = min(startIndex + partitionSize, tokenSequences.size)
            for (index in startIndex until endIndex) {
                val nGrams = tokenSequences[index].toNgrams(gramSize)
                nGrams.forEach { invertedIndex.hashTable.getOrPut(it) { mutableListOf() }.add(index to nGrams.size) }
            }
            return invertedIndex
        }
    }

    private val hashTable = mutableMapOf<NGram, MutableList<NGramInfo>>()
    operator fun get(key: NGram): List<NGramInfo> = hashTable[key] ?: emptyList()
}
