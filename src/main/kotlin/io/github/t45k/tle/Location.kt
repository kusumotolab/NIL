package io.github.t45k.tle

class Location(private val filteringThreshold: Int) {

    private val hashTable: MutableMap<Int, MutableList<Int>> = mutableMapOf()

    fun locate(seeds: List<Int>): List<Int> =
        seeds.flatMap { hashTable[it] ?: emptyList() }
            .groupingBy { it }
            .eachCount()
            .filter { it.value >= filteringThreshold }
            .keys
            .toList()

    fun put(seeds: List<Int>, id: Int) = seeds.forEach { hashTable.getOrPut(it) { mutableListOf() }.add(id) }
}
