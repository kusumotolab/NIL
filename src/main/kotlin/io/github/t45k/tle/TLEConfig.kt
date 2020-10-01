package io.github.t45k.tle

import io.github.t45k.tle.TokenizeMethod.LEXICAL_ANALYSIS
import io.github.t45k.tle.TokenizeMethod.SYMBOL_SEPARATION
import java.io.File

data class TLEConfig(
    val src: File,
    val tokenizeMethod: TokenizeMethod = SYMBOL_SEPARATION,
    val minLine: Int = 6,
    val windowSize: Int = 5,
    val filteringThreshold: Int = 70,
    val outputFileName: String = "result.csv",
)

enum class TokenizeMethod {
    LEXICAL_ANALYSIS,
    SYMBOL_SEPARATION,
}

fun parseArgs(args: Array<String>): TLEConfig {
    var src: File? = null
    var tokenizeMethod: TokenizeMethod = SYMBOL_SEPARATION
    var minLine = 6
    var windowSize = 5
    var filteringThreshold = 70
    var outputFileName: String? = null

    val iterator = args.iterator()
    while (iterator.hasNext()) {
        when (iterator.next()) {
            "-s" -> src = File(iterator.next())
            "-tm" -> tokenizeMethod = if (iterator.next() == "la") LEXICAL_ANALYSIS else SYMBOL_SEPARATION
            "-mil" -> minLine = iterator.next().toInt()
            "-ws" -> windowSize = iterator.next().toInt()
            "-ft" -> filteringThreshold = iterator.next().toInt()
            "-o" -> outputFileName = iterator.next()
        }
    }

    return TLEConfig(
        src!!,
        tokenizeMethod,
        minLine,
        windowSize,
        filteringThreshold,
        outputFileName ?: "result_${windowSize}_${filteringThreshold}.csv",
    )
}
