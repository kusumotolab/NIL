package io.github.t45k.nil

import java.io.File

data class NILConfig(
    val src: File,
    val minLine: Int = 6,
    val gramSize: Int = 5,
    val filteringThreshold: Int = 10,
    val outputFileName: String = "result.csv",
)

fun parseArgs(args: Array<String>): NILConfig {
    var src: File? = null
    var minLine = 6
    var gramSize = 5
    var filteringThreshold = 10
    var outputFileName: String? = null

    val iterator = args.iterator()
    while (iterator.hasNext()) {
        when (iterator.next()) {
            "-s" -> src = File(iterator.next())
            "-m" -> minLine = iterator.next().toInt()
            "-g" -> gramSize = iterator.next().toInt()
            "-f" -> filteringThreshold = iterator.next().toInt()
            "-o" -> outputFileName = iterator.next()
            else -> throw RuntimeException("Invalid option")
        }
    }

    return NILConfig(
        src!!,
        minLine,
        gramSize,
        filteringThreshold,
        outputFileName ?: "result_${gramSize}_${filteringThreshold}.csv",
    )
}
