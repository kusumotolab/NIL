package jp.ac.osaka_u.sdl.nil.usecase.cloneDetection

import jp.ac.osaka_u.sdl.nil.entity.NGramInfo
import kotlin.math.min

class NGramBasedFiltration(private val threshold: Int) : Filtration {
    override fun filter(nGramSize: Int, cloneCandidate: Map.Entry<NGramInfo, Int>): Boolean {
        val min = min(nGramSize, cloneCandidate.key.size)
        return cloneCandidate.value * 100 / min >= threshold
    }
}
