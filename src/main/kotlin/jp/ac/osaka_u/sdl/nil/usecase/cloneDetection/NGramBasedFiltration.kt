package jp.ac.osaka_u.sdl.nil.usecase.cloneDetection

import jp.ac.osaka_u.sdl.nil.entity.NGramInfo

class NGramBasedFiltration(private val threshold: Int) : Filtration, NGramSimilarity {
    override fun filter(nGramSize: Int, cloneCandidate: Map.Entry<NGramInfo, Int>): Boolean =
        calcSimilarity(nGramSize, cloneCandidate.key.size, cloneCandidate.value) >= threshold
}
