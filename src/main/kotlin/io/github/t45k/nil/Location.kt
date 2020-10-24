package io.github.t45k.nil

import io.github.t45k.nil.entity.CodeBlock
import io.github.t45k.nil.entity.TokenSequence
import kotlin.math.min

class Location(private val filteringThreshold: Int, private val codeBlocks: List<CodeBlock>) {

    private val hashTable: MutableMap<Int, MutableList<Int>> = HashMap(500_000)

    fun locate(tokenSequence: TokenSequence, index: Int): Sequence<Int> =
        sequence {
            val counts = Array(index) { 0 }
            val size = tokenSequence.size
            tokenSequence.flatMap { hashTable[it] ?: emptyList() }
                .forEach {
                    val base = min(size, codeBlocks[it].tokenSequence.size)
                    if (counts[it] * 100 / base >= filteringThreshold) {
                        return@forEach
                    }
                    counts[it]++
                    if (counts[it] * 100 / base >= filteringThreshold) {
                        yield(it)
                    }
                }
        }

    fun put(tokenSequence: TokenSequence, id: Int) =
        tokenSequence.forEach { hashTable.getOrPut(it) { mutableListOf() }.add(id) }

    fun clear() = hashTable.clear()
}
