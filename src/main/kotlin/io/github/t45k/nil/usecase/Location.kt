package io.github.t45k.nil.usecase

import io.github.t45k.nil.entity.NGramInfo
import io.github.t45k.nil.entity.NGrams
import io.reactivex.rxjava3.core.Flowable

interface Location {
    fun locate(nGrams: NGrams, index: Int): Flowable<Map.Entry<NGramInfo, Int>>
}
