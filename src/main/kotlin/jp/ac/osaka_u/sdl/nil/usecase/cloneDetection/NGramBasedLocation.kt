package jp.ac.osaka_u.sdl.nil.usecase.cloneDetection

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.toFlowable
import jp.ac.osaka_u.sdl.nil.entity.InvertedIndex
import jp.ac.osaka_u.sdl.nil.entity.NGramInfo
import jp.ac.osaka_u.sdl.nil.entity.NGrams

class NGramBasedLocation(private val invertedIndex: InvertedIndex) : Location {
    override fun locate(nGrams: NGrams, index: Int): Flowable<Map.Entry<NGramInfo, Int>> =
        nGrams.flatMap { invertedIndex[it] }
            .groupingBy { it }
            .eachCount()
            .asSequence()
            .toFlowable()
            .filter { (nGramInfo, _) -> index > nGramInfo.id }
}
