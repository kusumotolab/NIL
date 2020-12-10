package io.github.t45k.nil

import io.github.t45k.nil.entity.NGrams
import kotlin.math.min

class Location(private val config: NILConfig) {

    private val hashTable: MutableMap<Int, MutableList<Pair<ID, Size>>> = HashMap(config.partitionSize)

    fun locate(nGrams: NGrams): Sequence<Int> =
        mutableMapOf<Pair<ID, Size>, Int>().apply {
            nGrams.flatMap { hashTable[it] ?: emptyList() }
                .forEach { compute(it) { _, v -> if (v == null) 1 else v + 1 } }
        }.asSequence()
            .filter {
                val min = min(nGrams.size, it.key.second)
                it.value * 100 / min >= config.filteringThreshold
            }
            .map { it.key.first }

    fun put(nGrams: NGrams, id: Int) =
        nGrams.forEach { hashTable.getOrPut(it) { mutableListOf() }.add(id to nGrams.size) }

    fun clear() = hashTable.clear()
}

typealias ID = Int
typealias Size = Int
