package io.github.t45k.lvmapper

import java.util.concurrent.atomic.AtomicInteger

class ProgressMonitor(private val size: Int) {
    private val progress: AtomicInteger = AtomicInteger(1)

    init {
        print("0 -- ")
    }

    fun update(current: Int) {
        while (10 * current / size >= progress.get()) {
            if (progress.get() == 10) {
                println("100\n")
                break
            }

            print("${progress.getAndIncrement()}0 -- ")
        }
    }
}
