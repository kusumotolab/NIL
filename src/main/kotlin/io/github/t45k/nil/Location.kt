package io.github.t45k.nil

import io.github.t45k.nil.entity.CodeBlock
import io.github.t45k.nil.entity.TokenSequence
import kotlin.math.min

class Location(private val config: NILConfig, private val codeBlocks: List<CodeBlock>) {

    private val hashTable: MutableMap<Int, MutableList<Int>> = HashMap(config.partitionSize)

    fun locate(tokenSequence: TokenSequence): List<Int> =
        mutableMapOf<Int, Int>().apply {
            tokenSequence.flatMap { hashTable[it] ?: emptyList() }
                .forEach { compute(it) { _, v -> if (v == null) 1 else v + 1 } }
        }
            .filter {
                val min = min(tokenSequence.size, codeBlocks[it.key].tokenSequence.size)
                it.value * 100 / min >= config.filteringThreshold
            }
            .keys
            .toList()

    fun put(tokenSequence: TokenSequence, id: Int) =
        tokenSequence.forEach { hashTable.getOrPut(it) { mutableListOf() }.add(id) }

    fun clear() = hashTable.clear()
}
