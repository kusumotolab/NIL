package io.github.t45k.nil.usecase.cloneDetection

import io.github.t45k.nil.entity.NGramInfo

interface Filter {
    fun filter(nGramSize: Int, cloneCandidate: Map.Entry<NGramInfo, Int>): Boolean
}
