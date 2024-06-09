package jp.ac.osaka_u.sdl.nil.util

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.parallel.ParallelFlowable

fun <T : Any> Flowable<T>.parallelIfSpecified(threads: Int): ParallelFlowable<T> =
    if (threads > 0) {
        parallel(threads)
    } else {
        parallel()
    }
