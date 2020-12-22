package io.github.t45k.nil

import io.github.t45k.nil.entity.NGrams
import kotlin.math.min

class Location(private val config: NILConfig) {

    private val hashTable: MutableMap<Int, MutableList<Pair<ID, Size>>> = HashMap(config.partitionSize)

    fun locate(nGrams: NGrams): Sequence<Int> =
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
