package jp.ac.osaka_u.sdl.nil.usecase.cloneDetection

import io.reactivex.rxjava3.core.Flowable
import jp.ac.osaka_u.sdl.nil.entity.Id

interface CloneDetection {
    fun exec(id: Id): Flowable<Pair<Int, Int>>
}
