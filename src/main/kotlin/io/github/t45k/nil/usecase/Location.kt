package io.github.t45k.nil.usecase

import io.github.t45k.nil.entity.Id
import io.github.t45k.nil.entity.InvertedIndex
import io.github.t45k.nil.entity.NGrams
import kotlin.math.min

class Location(private val invertedIndex: InvertedIndex, private val threshold: Int) {
    fun collectCandidates(nGrams: NGrams): Sequence<Id> =
        nGrams.flatMap { invertedIndex[it] }
            .groupingBy { it }
            .eachCount()
            .asSequence()
            .filter {
                val min = min(nGrams.size, it.key.size)
                it.value * 100 / min >= threshold
            }
            .map { it.key.first }
}
