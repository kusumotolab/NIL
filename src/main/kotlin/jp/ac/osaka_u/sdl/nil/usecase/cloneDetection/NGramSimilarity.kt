package jp.ac.osaka_u.sdl.nil.usecase.cloneDetection

import kotlin.math.min

interface NGramSimilarity {
    fun calcSimilarity(sizeA: Int, sizeB: Int, intersection: Int): Int =
        intersection * 100 / min(sizeA, sizeB)
}
