package io.github.t45k.nil

import java.io.File

data class NILConfig(
    val src: File,
    val minLine: Int = 6,
    val minToken: Int = 50,
    val gramSize: Int = 5,
    val partitionSize: Int = 500_000,
    val filteringThreshold: Int = 10,
    val verifyingThreshold: Int = 70,
    val outputFileName: String = "result.csv",
)

fun parseArgs(args: Array<String>): NILConfig {
    var src: File? = null
    var minLine = 6
    var minToken = 50
    var gramSize = 5
    var partitionSize = 500_000
    var filteringThreshold = 10
    var verifyingThreshold = 70
    var outputFileName: String? = null

    val iterator = args.iterator()
    while (iterator.hasNext()) {
        when (val optionName = iterator.next()) {
            "-s", "--src" -> src = File(iterator.next())
            "-mil", "--min-line" -> minLine = iterator.next().toInt()
            "-mit", "--min-token" -> minToken = iterator.next().toInt()
            "-g", "--gram-size" -> gramSize = iterator.next().toInt()
            "-p", "--partition-size" -> partitionSize = iterator.next().toInt()
            "-f", "--filtering-threshold" -> filteringThreshold = iterator.next().toInt()
            "-v", "--verifying-threshold" -> verifyingThreshold = iterator.next().toInt()
            "-o", "--output" -> outputFileName = iterator.next()
            else -> throw InvalidOptionException(optionName)
        }
    }

    return NILConfig(
        src!!,
        minLine,
        minToken,
        gramSize,
        partitionSize,
        filteringThreshold,
        verifyingThreshold,
        outputFileName ?: "result_${gramSize}_${filteringThreshold}.csv",
    )
}

class InvalidOptionException(private val option: String) : RuntimeException() {
    override val message: String
        get() = """$option is invalid option.
            |-s, --src${'\t'}Source directory
            |-mil, --min-line${'\t'}Minimum line
            |-mit, --min-token${'\t'}Minimum token
            |-g, --gram-size${'\t'}N of N-gram
            |-p, --partition-size${'\t'}Size of partition
            |-f, --filtering-threshold${'\t'}Filtering threshold
            |-v, --verifying-threshold${'\t'}Verifying threshold
            |-o, --output${'\t'}Output file name
        """.trimMargin()
}
