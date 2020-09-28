package io.github.t45k.tle

import io.github.t45k.tle.TokenizeMethod.LEXICAL_ANALYSIS
import io.github.t45k.tle.TokenizeMethod.SYMBOL_SEPARATION
import java.io.File
import kotlin.math.max

data class LVMapperConfig(
    val src: File,
    val tokenizeMethod: TokenizeMethod = LEXICAL_ANALYSIS,
    val windowSize: Int = tokenizeMethod.windowSize,
    val filteringThreshold: Int = tokenizeMethod.filteringThreshold,
    val minToken: Int = tokenizeMethod.minToken,
    val maxToken: Int = tokenizeMethod.maxToken,
    val outputFileName: String = "result.csv",
)

enum class TokenizeMethod(
    val minToken: Int,
    val maxToken: Int,
    val windowSize: Int,
    val filteringThreshold: Int,
) {
    LEXICAL_ANALYSIS(50, 5000, 10, 10),
    SYMBOL_SEPARATION(20, 2000, 5, 5), ;
}

fun parseArgs(args: Array<String>): LVMapperConfig {
    var src: File? = null
    var tokenizeMethod: TokenizeMethod = SYMBOL_SEPARATION
    var windowSize: () -> Int = { tokenizeMethod.windowSize }
    var filteringThreshold: () -> Int = { tokenizeMethod.filteringThreshold }
    var minToken: () -> Int = { tokenizeMethod.minToken }
    var maxToken: () -> Int = { tokenizeMethod.maxToken }
    var outputFileName: String? = null

    val iterator = args.iterator()
    while (iterator.hasNext()) {
        when (iterator.next()) {
            "-s" -> src = File(iterator.next())
            "-tm" -> tokenizeMethod = if (iterator.next() == "la") LEXICAL_ANALYSIS else SYMBOL_SEPARATION
            "-mit" -> minToken = iterator.next().toInt().let { value -> { value } }
            "-mat" -> maxToken = iterator.next().toInt().let { value -> { value } }
            "-ws" -> windowSize = iterator.next().toInt().let { value -> { value } }
            "-ft" -> filteringThreshold = iterator.next().toInt().let { value -> { value } }
            "-o" -> outputFileName = iterator.next()
        }
    }

    return LVMapperConfig(
        src!!,
        tokenizeMethod,
        windowSize(),
        filteringThreshold(),
        max(minToken(), windowSize() + filteringThreshold() - 1),
        maxToken(),
        outputFileName ?: "result_${windowSize}_$filteringThreshold.csv",
    )
}
