package io.github.t45k.nil.core

import io.github.t45k.nil.NILConfig
import io.github.t45k.nil.entity.NGrams
import io.github.t45k.nil.entity.TokenSequence
import io.github.t45k.nil.entity.toNgrams
import kotlin.math.min

class Location private constructor(private val config: NILConfig) {

    companion object {
        /**
         * Create partial inverted index
         * @param config The configuration of NIL.
         * @param tokenSequences Entire token sequences of code blocks.
         * @param startIndex The index at which to start creating the partial inverted index.
         */
        fun from(config: NILConfig, tokenSequences: List<TokenSequence>, startIndex: Int): Location {
            val location = Location(config)
            val endIndex = min(startIndex + config.partitionSize, tokenSequences.size)
            for (index in startIndex until endIndex) {
                location.put(tokenSequences[index].toNgrams(config.gramSize), index)
            }
            return location
        }
    }

    private val hashTable: MutableMap<Int, MutableList<Pair<ID, Size>>> = HashMap(config.partitionSize)

    fun collectCandidates(nGrams: NGrams): Sequence<ID> =
        nGrams.flatMap { hashTable[it] ?: emptyList() }
            .groupingBy { it }
            .eachCount()
            .asSequence()
            .filter {
                val min = min(nGrams.size, it.key.second)
                it.value * 100 / min >= config.filteringThreshold
            }
            .map { it.key.first }

    fun put(nGrams: NGrams, id: Int) =
        nGrams.forEach { hashTable.getOrPut(it) { mutableListOf() }.add(id to nGrams.size) }
}

typealias ID = Int
typealias Size = Int
