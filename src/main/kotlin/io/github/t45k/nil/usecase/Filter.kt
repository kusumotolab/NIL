package io.github.t45k.nil.usecase

import io.github.t45k.nil.entity.NGramInfo
import kotlin.math.min

class Filter(private val threshold: Int) {
    fun filter(nGramSize: Int, cloneCandidate: Map.Entry<NGramInfo, Int>): Boolean {
        val min = min(nGramSize, cloneCandidate.key.size)
        return cloneCandidate.value * 100 / min >= threshold
    }
}
