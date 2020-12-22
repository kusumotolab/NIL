package io.github.t45k.nil.util

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.parallel.ParallelFlowable

fun <T> Flowable<T>.parallelIfSpecified(threads: Int): ParallelFlowable<T> =
    if (threads > 0) {
        parallel(threads)
    } else {
        parallel()
    }
