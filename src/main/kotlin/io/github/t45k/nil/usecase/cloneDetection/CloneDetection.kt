package io.github.t45k.nil.usecase.cloneDetection

import io.github.t45k.nil.entity.Id
import io.github.t45k.nil.entity.TokenSequence
import io.github.t45k.nil.entity.toNgrams
import io.reactivex.rxjava3.core.Flowable

class CloneDetection(
    private val locatingPhase: Location,
    private val filteringPhase: Filter,
    private val verifyingPhase: Verification,
    private val tokenSequences: List<TokenSequence>,
    private val gramSize: Int
) {
    fun exec(id: Id): Flowable<Pair<Int, Int>> {
        val nGrams = tokenSequences[id].toNgrams(gramSize)
        return locatingPhase.locate(nGrams, id)
            .filter { filteringPhase.filter(nGrams.size, it) }
            .map { it.key.id }
            .filter { verifyingPhase.verify(tokenSequences[id], tokenSequences[it]) }
            .map { it to id }
    }
}
