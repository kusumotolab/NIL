package io.github.t45k.tle

import java.io.File

data class TLEConfig(
    val src: File,
    val minLine: Int = 6,
    val gramSize: Int = 5,
    val filteringThreshold: Int = 70,
    val outputFileName: String = "result.csv",
)

fun parseArgs(args: Array<String>): TLEConfig {
    var src: File? = null
    var minLine = 6
    var windowSize = 5
    var filteringThreshold = 70
    var outputFileName: String? = null

    val iterator = args.iterator()
    while (iterator.hasNext()) {
        when (iterator.next()) {
            "-s" -> src = File(iterator.next())
            "-m" -> minLine = iterator.next().toInt()
            "-g" -> windowSize = iterator.next().toInt()
            "-f" -> filteringThreshold = iterator.next().toInt()
            "-o" -> outputFileName = iterator.next()
            else -> throw RuntimeException("Invalid option")
        }
    }

    return TLEConfig(
        src!!,
        minLine,
        windowSize,
        filteringThreshold,
        outputFileName ?: "result_${windowSize}_${filteringThreshold}.csv",
    )
}
