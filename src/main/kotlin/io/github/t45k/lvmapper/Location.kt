package io.github.t45k.lvmapper

class Location {
    companion object {
        private const val FILTERING_THRESHOLD = 5
    }

    private val hashTable: MutableMap<Int, MutableList<Int>> = mutableMapOf()

    fun locate(seeds: List<Int>): List<Int> =
        seeds.flatMap { hashTable[it] ?: emptyList() }
            .groupingBy { it }
            .eachCount()
            .filter { it.value >= FILTERING_THRESHOLD }
            .keys
            .toList()

    fun put(seeds: List<Int>, id: Int) = seeds.forEach { hashTable.getOrPut(it) { mutableListOf() }.add(id) }
}
