package io.github.t45k.nil

import io.github.t45k.nil.entity.CodeBlock
import io.github.t45k.nil.entity.TokenSequence
import kotlin.math.min

class Location(private val filteringThreshold: Int, private val codeBlocks: List<CodeBlock>) {

    private val hashTable: MutableMap<Int, MutableList<Int>> = HashMap(500_000)

    fun locate(tokenSequence: TokenSequence): Sequence<Int> =
        sequence {
            hashMapOf<Int, Int>().apply {
                tokenSequence
                    .flatMap { hashTable[it] ?: emptyList() }
                    .forEach {
                        val base = min(tokenSequence.size, codeBlocks[it].tokenSequence.size)
                        if (this[it] != null && this[it]!! / base >= filteringThreshold) {
                            return@forEach
                        }
                        compute(it) { _, v -> if (v == null) 1 else v + 1 }
                        if (this[it]!! / base >= filteringThreshold) {
                            yield(it)
                        }
                    }
            }
        }

    fun put(tokenSequence: TokenSequence, id: Int) =
        tokenSequence.forEach { hashTable.getOrPut(it) { mutableListOf() }.add(id) }

    fun clear() = hashTable.clear()
}
