package io.github.t45k.tle

import io.github.t45k.tle.entity.CodeBlock
import kotlin.math.min

class Location(private val filteringThreshold: Int, private val codeBlocks: List<CodeBlock>) {

    private val hashTable: MutableMap<Int, MutableList<Int>> = mutableMapOf()

    fun locate(seeds: List<Int>): List<Int> =
        seeds.flatMap { hashTable[it] ?: emptyList() }
            .groupingBy { it }
            .eachCount()
            .filter { it.value * 100 / min(seeds.size, codeBlocks[it.key].seedsSize) >= filteringThreshold }
            .keys
            .toList()

    fun put(seeds: List<Int>, id: Int) = seeds.forEach { hashTable.getOrPut(it) { mutableListOf() }.add(id) }
}
