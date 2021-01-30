package jp.ac.osaka_u.sdl.nil.usecase.cloneDetection

import io.reactivex.rxjava3.core.Flowable
import jp.ac.osaka_u.sdl.nil.entity.NGramInfo
import jp.ac.osaka_u.sdl.nil.entity.NGrams

interface Location {
    fun locate(nGrams: NGrams, index: Int): Flowable<Map.Entry<NGramInfo, Int>>
}
