package jp.ac.osaka_u.sdl.nil.usecase.cloneDetection

import jp.ac.osaka_u.sdl.nil.entity.NGramInfo

interface Filtration {
    fun filter(nGramSize: Int, cloneCandidate: Map.Entry<NGramInfo, Int>): Boolean
}
