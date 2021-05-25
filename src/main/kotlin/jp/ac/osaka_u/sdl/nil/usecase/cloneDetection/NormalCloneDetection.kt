package jp.ac.osaka_u.sdl.nil.usecase.cloneDetection

import io.reactivex.rxjava3.core.Flowable
import jp.ac.osaka_u.sdl.nil.entity.Id
import jp.ac.osaka_u.sdl.nil.entity.TokenSequence
import jp.ac.osaka_u.sdl.nil.entity.toNgrams

class NormalCloneDetection(
    private val locatingPhase: Location,
    private val filteringPhase: Filtration,
    private val verifyingPhase: Verification,
    private val tokenSequences: List<TokenSequence>,
    private val gramSize: Int
) : CloneDetection {
    override fun exec(id: Id): Flowable<Pair<Int, Int>> {
        val nGrams = tokenSequences[id].toNgrams(gramSize)
        return locatingPhase.locate(nGrams, id)
            .filter { filteringPhase.filter(nGrams.size, it) }
            .map { it.key.id }
            .filter { verifyingPhase.verify(tokenSequences[id], tokenSequences[it]) }
            .map { it to id }
    }
}
