package io.github.t45k.nil.usecase

import io.github.t45k.nil.entity.InvertedIndex
import io.github.t45k.nil.entity.NGramInfo
import io.github.t45k.nil.entity.NGrams
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.toFlowable

class Location(private val invertedIndex: InvertedIndex) {
    fun locate(nGrams: NGrams, index: Int): Flowable<Map.Entry<NGramInfo, Int>> =
        nGrams.flatMap { invertedIndex[it] }
            .groupingBy { it }
            .eachCount()
            .asSequence()
            .toFlowable()
            .filter { (nGramInfo, _) -> index > nGramInfo.id }
}
