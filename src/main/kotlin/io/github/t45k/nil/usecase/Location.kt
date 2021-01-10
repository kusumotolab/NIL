package io.github.t45k.nil.usecase

import io.github.t45k.nil.entity.Id
import io.github.t45k.nil.entity.InvertedIndex
import io.github.t45k.nil.entity.NGrams
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.toFlowable
import kotlin.math.min

class Location(private val invertedIndex: InvertedIndex, private val threshold: Int) {
    fun collectCandidates(nGrams: NGrams): Flowable<Id> =
        nGrams.flatMap { invertedIndex[it] }
            .groupingBy { it }
            .eachCount()
            .asSequence()
            .toFlowable()
            .filter { (nGramInfo, count) ->
                val min = min(nGrams.size, nGramInfo.size)
                count * 100 / min >= threshold
            }
            .map { it.key.id }
}
