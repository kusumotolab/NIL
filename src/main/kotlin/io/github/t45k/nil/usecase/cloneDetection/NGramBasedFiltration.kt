package io.github.t45k.nil.usecase.cloneDetection

import io.github.t45k.nil.entity.NGramInfo
import kotlin.math.min

class NGramBasedFiltration(private val threshold: Int) : Filtration {
    override fun filter(nGramSize: Int, cloneCandidate: Map.Entry<NGramInfo, Int>): Boolean {
        val min = min(nGramSize, cloneCandidate.key.size)
        return cloneCandidate.value * 100 / min >= threshold
    }
}
