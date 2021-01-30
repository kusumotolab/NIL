package jp.ac.osaka_u.sdl.nil.util

import java.util.concurrent.atomic.AtomicInteger

class ProgressMonitor(private val size: Int) {
    private val progress: AtomicInteger = AtomicInteger(1)
    private var time: Long = System.currentTimeMillis()

    init {
        print("0(0s) -- ")
    }

    fun update(current: Int) {
        while (10 * current / size >= progress.get()) {
            val currentTimeMillis = System.currentTimeMillis()
            val interval = (currentTimeMillis - time) / 1000
            time = currentTimeMillis
            if (progress.get() == 10) {
                println("100(${interval.toTime()})\n")
                break
            }

            print("${progress.getAndIncrement()}0(${interval.toTime()}) -- ")
        }
    }
}
