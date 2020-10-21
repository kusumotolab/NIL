package io.github.t45k.nil

import io.github.t45k.nil.entity.CodeBlock
import io.github.t45k.nil.entity.NGrams
import kotlin.math.min

class Location(private val filteringThreshold: Int, private val codeBlocks: List<CodeBlock>) {

    private val hashTable: MutableMap<Int, MutableList<Int>> = mutableMapOf()

    fun locate(nGrams: NGrams): List<Int> =
        mutableMapOf<Int, Int>().apply {
            nGrams.flatMap { hashTable[it] ?: emptyList() }
                .forEach { compute(it) { _, v -> if (v == null) 1 else v + 1 } }
        }
            .filter { it.value * 100 / min(nGrams.size, codeBlocks[it.key].nGrams.size) >= filteringThreshold }
            .keys
            .toList()

    fun put(nGrams: NGrams, id: Int) = nGrams.forEach { hashTable.getOrPut(it) { mutableListOf() }.add(id) }

    fun clear() = hashTable.clear()
}
