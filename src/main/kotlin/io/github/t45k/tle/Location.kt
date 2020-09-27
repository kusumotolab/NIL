package io.github.t45k.tle

import kotlin.math.max
import kotlin.math.min

class Location(private val filteringThreshold: Int) {

    private val hashTable: MutableMap<Int, MutableList<Int>> = mutableMapOf()

    fun locate(seeds: List<Int>): List<Int> =
        seeds.flatMap { hashTable[it] ?: emptyList() }
            .groupingBy { it }
            .eachCount()
            .filter { min(seeds.size, it.value) * 10 / max(seeds.size, it.value) >= filteringThreshold }
            .keys
            .toList()

    fun put(seeds: List<Int>, id: Int) = seeds.forEach { hashTable.getOrPut(it) { mutableListOf() }.add(id) }
}
