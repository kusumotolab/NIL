package io.github.t45k.tle

import io.github.t45k.tle.entity.CodeBlock
import io.github.t45k.tle.entity.NGrams
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.toObservable
import kotlin.math.min

class Location(private val filteringThreshold: Int, private val codeBlocks: List<CodeBlock>) {

    private val hashTable: MutableMap<Int, MutableList<Int>> = mutableMapOf()

    fun locate(nGrams: NGrams): Observable<Int> =
        nGrams.flatMap { hashTable[it] ?: emptyList() }
            .groupingBy { it }
            .eachCount()
            .filter { it.value * 100 / min(nGrams.size, codeBlocks[it.key].nGrams.size) >= filteringThreshold }
            .keys
            .toObservable()

    fun put(nGrams: NGrams, id: Int) = nGrams.forEach { hashTable.getOrPut(it) { mutableListOf() }.add(id) }
}
